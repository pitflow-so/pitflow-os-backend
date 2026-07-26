# Architecture Decision Records (ADRs) - Pitflow OS

## ADR 001: Padrão de Arquitetura de Software e Comunicação
**Data:** Maio de 2026
**Status:** Aceito ✅

* **Contexto:** O domínio de Ordens de Serviço possui regras de negócio complexas (transições de status, cálculos de valores, histórico de tempo) que precisam evoluir de forma independente das tecnologias de banco de dados ou frameworks web.
* **Decisão:** Adoção da **Clean Architecture** orientada por princípios de **Domain-Driven Design (DDD)**. Para comunicação externa e entre clientes web/mobile, padronizou-se o uso de **APIs RESTful** síncronas em JSON.
* **Consequências:** 
  * **Positivas:** Alto isolamento do core de negócio. Facilidade de criação de testes unitários sem dependência de infraestrutura (banco, web).
  * **Negativas:** Maior curva de aprendizado inicial e verbosidade no código devido à necessidade de mapeadores (Mappers) entre entidades de domínio e DTOs/Entities de infraestrutura.

---

## ADR 002: Automação e Orquestração (IaC e CI/CD) via GitHub Actions
**Data:** Maio de 2026
**Status:** Aceito ✅

* **Contexto:** Múltiplos repositórios compõem o ecossistema (Bootstrap, Database, Cluster, Backend, Lambdas). A criação manual de recursos (ClickOps) geraria inconsistências, falta de rastreabilidade e falhas de integração contínua.
* **Decisão:** Utilização exclusiva do **GitHub Actions** como orquestrador central. Ele é responsável por disparar pipelines de Infraestrutura como Código (**Terraform**) e realizar o build, push (ECR) e deploy (Kubernetes) da aplicação.
* **Consequências:**
    * **Positivas:** Rastreabilidade total das mudanças de infraestrutura. Ambientes reproduzíveis e deploys automatizados baseados em eventos do repositório (`push` na branch main).
    * **Negativas:** Forte acoplamento ao ecossistema do GitHub. Necessidade de gestão rigorosa de permissões (roles) entre o GitHub e a AWS.

---

## ADR 003: Gestão Centralizada de Dados Sensíveis via AWS Secrets Manager
**Data:** Maio de 2026
**Status:** Aceito ✅

* **Contexto:** Senhas de banco de dados, chaves de API (Datadog) e segredos criptográficos (JWT Secret) não podem, sob nenhuma hipótese, transitar em texto plano nos logs das pipelines ou ficar hardcodados em arquivos `.yaml` e código-fonte.
* **Decisão:** Adoção do **AWS Secrets Manager** como cofre único (Single Source of Truth). As GitHub Actions gravam e leem os valores deste cofre e injetam de forma segura no Kubernetes (via `secretKeyRef` no Deployment) e nas Lambdas no momento do provisionamento.
* **Consequências:**
    * **Positivas:** Conformidade total com boas práticas de segurança. Auditoria centralizada de quem e o que acessa as credenciais.
    * **Negativas:** Adiciona uma chamada externa de rede durante a esteira de deploy e um leve custo operacional associado ao serviço da AWS.

---

## ADR 004: Escalabilidade Dinâmica com Horizontal Pod Autoscaler (HPA)
**Data:** Maio de 2026
**Status:** Aceito ✅

* **Contexto:** A oficina mecânica pode possuir picos de utilização do sistema durante horários específicos. Manter um número fixo de instâncias do backend Spring Boot gera desperdício de recursos financeiros e risco de gargalos no pico.
* **Decisão:** Implementação do **Horizontal Pod Autoscaler (HPA)** no cluster EKS. O HPA monitora o consumo de CPU e Memória (coletados pelo Datadog Agent/Metrics Server) e ajusta dinamicamente a quantidade de réplicas do deployment `pitflow-backend`.
* **Consequências:**
    * **Positivas:** Alta disponibilidade garantida durante picos de tráfego e otimização drástica de custos fora do horário de pico.
    * **Negativas:** Exige calibração fina (tuning) dos `requests` e `limits` de hardware no Kubernetes, além do ajuste preciso das *Readiness/Liveness Probes* para evitar que o cluster mate pods saudáveis sob carga.

---

## ADR 005: Transactional Outbox com Publisher Agendado na Aplicação

**Data:** Julho de 2026
**Status:** Aceito ✅

### Contexto

A aprovação do orçamento precisa atualizar a Ordem de Serviço no PostgreSQL e
publicar `ServiceOrderBudgetApproved` na SQS. Não existe transação distribuída
ACID entre PostgreSQL e SQS.

Publicar diretamente antes do commit pode enviar um evento sobre uma alteração
que posteriormente sofreu rollback. Publicar somente depois do commit, sem
persistir a intenção, pode perder o evento se o pod cair entre o commit e o
envio.

### Decisão

Adotar o padrão **Transactional Outbox**:

1. alterar a Ordem de Serviço para `PAYMENT_PROCESSING`;
2. inserir o envelope do evento em `operation_outbox` com status `PENDING`;
3. confirmar as duas escritas na mesma transação PostgreSQL;
4. publicar posteriormente a mensagem na SQS;
5. marcar a outbox como `PUBLISHED` somente após confirmação do SQS.

O publisher será um componente de infraestrutura dentro da mesma aplicação
Spring Boot e da mesma JVM/container do Operation. Não haverá serviço, pod ou
container exclusivo para outbox.

```text
Pod do Operation
├── API REST
├── casos de uso
├── scheduler da outbox
└── publisher SQS
```

### Scheduler, claim e lease

Cada réplica do Operation terá um scheduler. Para permitir HPA sem publicação
concorrente do mesmo registro, o scheduler executará:

1. selecionar um lote pequeno de mensagens `PENDING` cujo `available_at` já
   venceu;
2. aplicar `FOR UPDATE SKIP LOCKED`;
3. mudar os registros para `PROCESSING`, incrementando `attempts`;
4. atribuir `lock_id` e `locked_until`;
5. confirmar o claim rapidamente, sem manter transação aberta durante chamada
   de rede;
6. enviar cada payload à fila indicada em `destination`;
7. marcar como `PUBLISHED` em sucesso;
8. em falha, voltar para `PENDING`, registrar erro resumido e calcular
   `available_at` com backoff.

Um registro `PROCESSING` com lease vencido volta a ser elegível. Assim, se um
pod cair após o claim, outra réplica recupera o trabalho.

Parâmetros iniciais:

| Parâmetro | Valor inicial |
|---|---:|
| Delay do scheduler | 5 segundos |
| Tamanho do lote | 10 |
| Lease | 60 segundos |
| Backoff | exponencial com limite |
| Threads do publisher por pod | 1 |

Esses valores serão configuráveis e ajustados por métricas, não hardcoded na
regra de negócio.

### Tentativa imediata após o commit

Uma tentativa disparada por `@TransactionalEventListener(AFTER_COMMIT)` pode
reduzir a latência. Ela é apenas uma otimização. O scheduler continua sendo o
mecanismo de recuperação obrigatório.

Se o processo cair após o commit e antes do listener, a mensagem permanece
`PENDING`.

### Semântica de entrega e idempotência

A garantia é **at least once**, não exactly once.

- queda antes do envio: lease expira e a mensagem é reenviada;
- queda depois do envio e antes de `PUBLISHED`: a SQS pode receber duplicata;
- SQS Standard também pode duplicar ou reordenar mensagens;
- consumidores devem manter inbox/idempotência por `messageId`;
- eventos fora de ordem não podem avançar indevidamente a máquina de estados.

O `messageId` da outbox é estável entre retries. Não gerar um novo identificador
a cada tentativa.

### Separação por Clean Architecture

- **Core:** evento `ServiceOrderBudgetApproved` e
  `OperationEventGateway`;
- **Use case:** altera a OS e registra o evento por meio das portas;
- **Infrastructure/persistence:** tabela, entidade JPA, serialização e
  claim/lease;
- **Infrastructure/messaging:** adapter AWS SQS e scheduler;
- Spring, JPA, scheduler e AWS SDK não entram no domínio.

### Observabilidade

Publicar no mínimo:

- quantidade de mensagens `PENDING` e `PROCESSING`;
- idade da mensagem pendente mais antiga;
- publicações concluídas;
- retries e falhas;
- leases recuperados;
- tempo entre `created_at` e `published_at`;
- correlação por `messageId`, `correlationId`, `serviceOrderId` e `sagaId`,
  sem registrar payload sensível.

### Consequências

**Positivas:**

- não perde a intenção de publicar após o commit;
- funciona com múltiplas réplicas;
- mantém domínio independente de Spring e AWS;
- falhas de SQS não fazem rollback do negócio já confirmado;
- permite auditoria e recuperação operacional.

**Negativas:**

- consistência entre banco e fila é eventual;
- exige tabela, scheduler, retry, métricas e limpeza histórica;
- duplicatas continuam possíveis e precisam ser tratadas no consumidor;
- configuração incorreta do lease pode causar atraso ou concorrência.

### Alternativas rejeitadas

- **Publicação síncrona dentro da transação:** mantém conexão/transação aberta
  durante I/O e ainda não cria atomicidade com SQS.
- **Publicação somente `AFTER_COMMIT`:** baixa latência, mas perde evento se o
  processo cair antes do envio.
- **Serviço separado de outbox:** aumenta implantação e custo sem necessidade
  para o volume e prazo atuais.
- **CDC/Debezium:** solução robusta em escala maior, porém adiciona
  infraestrutura e complexidade desproporcionais ao MVP.

### Referências

- `pitflow-bootstrap/docs/adr/ADR-001-saga-messaging.md`;
- `pitflow-bootstrap/contracts/asyncapi/pitflow-saga-v1.yaml`;
- `src/main/resources/db/changelog/migrations/002-create-operation-outbox.sql`.
