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