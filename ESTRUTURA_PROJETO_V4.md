# Estrutura do Projeto - Pitflow OS Backend - Versao 4

## Objetivo deste documento

Este documento registra uma revisao completa da estrutura atual do projeto `pitflow-os-backend`, considerando:

- organizacao do codigo-fonte;
- camadas presentes na aplicacao;
- aderencia aos principios de clean architecture;
- justificativas para a arquitetura adotada;
- camada operacional de CI/CD, IaC, clusterizacao e alta disponibilidade;
- camada de seguranca com JWT;
- integracoes externas por webhook.

O texto foi montado a partir da estrutura real do repositorio e dos arquivos principais de codigo, configuracao e infraestrutura.

## Visao geral do repositorio

O projeto hoje combina duas dimensoes complementares:

1. uma aplicacao backend em Java 21 com Spring Boot 4, organizada por contexto de negocio;
2. uma camada operacional completa para build, deploy e execucao em nuvem.

Em termos praticos, o repositorio cobre:

- dominio e casos de uso da aplicacao;
- adaptadores web e persistencia;
- autenticacao e autorizacao com JWT;
- migracoes de banco com Liquibase;
- empacotamento com Docker;
- provisionamento de infraestrutura com Terraform;
- deploy em cluster Kubernetes;
- pipeline CI/CD com GitHub Actions.

## Estrutura raiz do projeto

```text
pitflow-os-backend/
|-- .github/
|   `-- workflows/
|       `-- main.yaml
|-- img/
|-- infra/
|   |-- k8s/
|   |   |-- configmap.yaml
|   |   |-- deployment.yaml
|   |   |-- hpa.yaml
|   |   |-- secrets.yaml
|   |   `-- service.yaml
|   `-- terraform/
|       |-- backend.tf
|       |-- ecr.tf
|       |-- eks.tf
|       |-- provider.tf
|       |-- rds.tf
|       |-- terraform.tfvars
|       `-- variables.tf
|-- src/
|   |-- main/
|   |   |-- java/br/com/pitflow/
|   |   `-- resources/
|   `-- test/
|       |-- java/br/com/pitflow/
|       `-- resources/
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
|-- README.md
|-- HOMOLOGACAO.md
|-- ESTRUTURA_PROJETO.md
|-- ESTRUTURA_PROJETO_V2.md
|-- ESTRUTURA_PROJETO_V3.md
`-- ESTRUTURA_PROJETO_V4.md
```

## Modulos da aplicacao

O codigo principal fica em `src/main/java/br/com/pitflow` e esta organizado em quatro modulos:

- `common`
- `registry`
- `inventory`
- `operation`

Essa divisao e importante porque reduz o risco de um pacote unico crescer sem fronteiras e permite alinhar o codigo a capacidades de negocio distintas.

### `common`

Concentra preocupacoes transversais:

- gateways compartilhados, como `TokenGateway`, `PasswordVerifierGateway` e `TransactionGateway`;
- configuracao Spring;
- filtro de seguranca;
- servico JWT;
- tratamento global de excecoes;
- adaptador transacional.

Esse modulo funciona como infraestrutura compartilhada da aplicacao, mas sem carregar regras do negocio principal.

### `registry`

Responsavel por cadastro e autenticacao:

- clientes;
- mecanicos;
- veiculos;
- login de mecanicos.

### `inventory`

Responsavel pelo catalogo da oficina:

- pecas;
- servicos.

### `operation`

Responsavel pelo ciclo de vida da ordem de servico:

- abertura da OS;
- diagnostico;
- aprovacao;
- execucao;
- finalizacao;
- entrega;
- cancelamento;
- metricas de execucao;
- eventos externos via webhook.

## Estrutura interna por contexto

Os modulos de negocio seguem uma estrutura muito proxima entre si:

```text
modulo/
|-- controller/
|   `-- dto/
|-- core/
|   |-- entity/
|   |-- gateway/
|   |-- usecase/
|   |-- outputData/   (quando aplicavel)
|   `-- valueObject/  (quando aplicavel)
|-- infrastructure/
|   |-- config/
|   |-- persistence/
|   |   |-- adapter/
|   |   |-- entity/
|   |   |-- mapper/
|   |   `-- repository/
|   |-- security/      (quando aplicavel)
|   |-- notifications/ (quando aplicavel)
|   `-- web/
|       `-- dto/
`-- presenter/
    `-- dto/
```

Essa padronizacao e uma das evidencias mais fortes da refatoracao: os modulos passaram a ter a mesma linguagem estrutural, o que facilita manutencao, onboarding e evolucao incremental.

## Camadas presentes na aplicacao

Do ponto de vista arquitetural, o projeto ja possui camadas bem definidas.

### 1. Camada de dominio e aplicacao: `core`

A pasta `core` concentra:

- entidades de negocio;
- contratos de acesso externo por meio de gateways;
- casos de uso;
- objetos de valor;
- modelos de saida de aplicacao em alguns fluxos.

Exemplos:

- `registry/core/entity/Customer.java`
- `registry/core/valueObject/CpfCnpj.java`
- `inventory/core/gateway/PartGateway.java`
- `operation/core/usecase/ApproveOrderImp.java`

Justificativa arquitetural:

- o dominio fica isolado da tecnologia;
- regras de negocio podem ser testadas sem depender de Spring MVC ou JPA;
- a dependencia aponta para contratos, nao para implementacoes concretas.

### 2. Camada de entrada e orquestracao: `controller` e `infrastructure/web`

O projeto hoje possui duas pecas relevantes na borda HTTP:

- `controller/`, que orquestra comandos e respostas na linguagem da aplicacao;
- `infrastructure/web/`, que expoe endpoints REST com anotacoes Spring.

Exemplos:

- `registry/infrastructure/web/AuthRestAdapter.java`
- `inventory/infrastructure/web/PartRestAdapter.java`
- `operation/infrastructure/web/ServiceOrderRestAdapter.java`
- `operation/infrastructure/web/ExternalEventRestAdapter.java`

Justificativa arquitetural:

- o `RestAdapter` isola o framework HTTP;
- o `controller` centraliza a traducao entre request DTO, comando de use case e presenter.

Observacao importante:

- a coexistencia de `controller` e `infrastructure/web` mostra um estado de transicao entre a estrutura legada e um modelo mais aderente a clean architecture;
- isso nao invalida a arquitetura, mas indica que a borda ainda pode ser consolidada em uma convencao unica no futuro.

### 3. Camada de saida: `presenter`

Todos os contextos de negocio possuem `presenter`, com DTOs proprios de resposta.

Responsabilidades:

- transformar entidades em respostas HTTP;
- impedir que a serializacao publica exponha diretamente o modelo interno;
- manter a formatacao de saida fora dos casos de uso.

Justificativa arquitetural:

- protege o dominio de preocupacoes de API;
- permite evoluir a resposta externa sem contaminar a regra de negocio;
- reforca a separacao entre modelo interno e contrato externo.

### 4. Camada de infraestrutura: `infrastructure`

Essa camada implementa tudo o que o dominio nao deve conhecer diretamente:

- persistencia;
- seguranca;
- configuracao;
- notificacao;
- web adapters.

Justificativa arquitetural:

- tecnologias externas ficam substituiveis;
- a aplicacao passa a depender de portas, e nao de frameworks ou bancos especificos.

## Aderencia a clean architecture

O projeto esta bem alinhado com clean architecture por quatro razoes principais.

### Separacao entre regras e detalhes tecnicos

As regras do negocio estao em `core`, enquanto JPA, HTTP, JWT, Spring Security e transacao estao fora dele.

Isso segue o principio central da clean architecture:

- regras de negocio no centro;
- detalhes tecnicos nas bordas.

### Inversao de dependencia

Os casos de uso dependem de gateways abstratos.

Exemplos:

- `MechanicGateway`
- `CustomerGateway`
- `VehicleGateway`
- `PartGateway`
- `ServiceGateway`
- `ServiceOrderGateway`
- `NotificationGateway`
- `TokenGateway`
- `TransactionGateway`

As implementacoes concretas estao em adaptadores como:

- `JpaCustomerGatewayAdapter`
- `JpaServiceOrderGatewayAdapter`
- `BcryptPasswordVerifierAdapter`
- `JwtServiceImp`
- `SpringTransactionAdapter`

Justificativa:

- o dominio nao conhece banco, hash, token ou framework;
- a troca de tecnologia exige mudanca nas bordas, nao no centro da aplicacao.

### Casos de uso explicitos

Os fluxos relevantes estao nomeados como classes de use case, em vez de serem embutidos em controller ou service generico.

Exemplos no modulo `operation`:

- `CreateServiceOrderImp`
- `CompleteDiagnosisImp`
- `ApproveOrderImp`
- `FinishOrderImp`
- `GetAverageExecutionTimeImp`
- `ListPrioritizedServiceOrdersImp`

Justificativa:

- torna o negocio mais legivel;
- melhora cobertura de testes;
- facilita rastrear responsabilidades.

### Wiring por configuracao

Os beans sao compostos explicitamente por configuracoes como:

- `BeanSecurityConfig`
- `BeanRegistryConfig`
- `BeanInventoryConfig`
- `BeanOperationConfig`

Justificativa:

- o acoplamento entre portas e adaptadores fica visivel;
- a aplicacao ganha previsibilidade na montagem das dependencias;
- reforca a leitura arquitetural do sistema.

## Fluxo arquitetural predominante

O fluxo atual da aplicacao pode ser lido assim:

```text
HTTP Request
  -> RestAdapter
  -> Controller
  -> Input Port / Use Case
  -> Entity + Business Rules
  -> Gateway Interface
  -> Infrastructure Adapter
  -> JPA / PostgreSQL / External Mechanism
  -> Presenter
  -> HTTP Response
```

Esse fluxo demonstra boa separacao entre entrada, negocio, integracao e saida.

## Persistencia e acesso a dados

Os modulos `registry`, `inventory` e `operation` repetem o mesmo padrao em persistencia:

```text
infrastructure/persistence/
|-- adapter/
|-- entity/
|-- mapper/
`-- repository/
```

Leitura de cada subcamada:

- `adapter/`: implementa o gateway definido no `core`;
- `entity/`: representa o modelo persistente JPA;
- `mapper/`: converte dominio para persistencia e vice-versa;
- `repository/`: interfaces Spring Data.

Justificativa arquitetural:

- mantem o dominio desacoplado de JPA;
- evita misturar anotacoes de persistencia nas entidades centrais;
- facilita mudanca futura de mecanismo de armazenamento.

## Camada transacional

O projeto introduziu uma abstracao de transacao:

- `common/core/gateway/TransactionGateway`
- `common/infrastructure/transaction/SpringTransactionAdapter`

Essa decisao e importante porque permite tratar transacao como detalhe de infraestrutura, nao como anotacao espalhada diretamente nos use cases.

Ponto relevante:

- existe inclusive um comentario no fluxo de criacao da OS com todos os dados indicando preocupacao com consistencia transacional;
- isso mostra maturidade arquitetural, porque a transacao esta sendo tratada como politica de infraestrutura aplicada a um caso de uso composto.

## Seguranca

O projeto possui uma camada de seguranca relativamente clara e bem encapsulada.

### JWT

Componentes centrais:

- `TokenGateway`
- `JwtServiceImp`
- `SecurityFilter`
- `SecurityConfig`
- `OpenApiConfig`

Funcionamento observado:

1. o mecanico autentica via `/registry/auth/login`;
2. `AuthenticateMechanicImp` valida usuario e senha;
3. a senha e conferida por `PasswordVerifierGateway`, implementado com bcrypt;
4. um token JWT e gerado com claims como `name` e `role`;
5. o `SecurityFilter` recupera o token Bearer do header `Authorization`;
6. o filtro valida o token e injeta autenticacao no contexto do Spring Security.

Justificativa arquitetural:

- autenticacao fica desacoplada do controller;
- geracao e validacao de token viram servicos de borda;
- a regra de autenticacao continua expressa em caso de uso.

### BCrypt

O projeto tambem separa:

- codificacao de senha;
- verificacao de senha.

Isso aparece com:

- `PasswordEncoderGateway`
- `PasswordVerifierGateway`
- `BcryptPasswordEncoderAdapter`
- `BcryptPasswordVerifierAdapter`

Justificativa:

- evita acoplamento direto da regra de cadastro/login ao encoder concreto;
- facilita testes e mudancas de algoritmo.

### Politica de acesso

O `SecurityConfig` indica:

- sessao `STATELESS`;
- CSRF desabilitado por uso de JWT;
- health check liberado;
- Swagger liberado;
- autenticacao liberada;
- alguns endpoints publicos de cadastro e consulta;
- webhook externo liberado por `PATCH /external/events/service-orders/**`.

Leitura arquitetural:

- a seguranca esta centralizada;
- as excecoes de autorizacao estao declaradas explicitamente.

Ponto de atencao:

- alguns endpoints de negocio criticos estao com `permitAll`, como criacao de OS, aprovacao, cancelamento e consulta de status;
- isso pode ser intencional para MVP ou integracao externa, mas deve ser documentado como decisao consciente de seguranca.

## Webhooks e integracoes externas

O projeto possui uma integracao explicita por webhook no modulo `operation`.

Endpoint identificado:

- `PATCH /external/events/service-orders/status-update`

Arquivo principal:

- `operation/infrastructure/web/ExternalEventRestAdapter.java`

Payload:

- `ExternalStatusUpdateRequest`
- eventos suportados: `APPROVED`, `REJECTED`, `FINISHED`

Comportamento:

- o adapter recebe o evento externo;
- delega para `ServiceOrderController.processExternalStatusUpdate`;
- o controller traduz o evento em acao do caso de uso adequado;
- o estado da OS e atualizado de acordo com as regras de negocio.

Justificativa arquitetural:

- o webhook fica na borda da aplicacao, como deveria;
- a regra de negocio permanece centralizada no fluxo da OS;
- integracao externa nao invade o dominio com detalhes HTTP.

Leitura complementar:

- tambem existe o endpoint `PATCH /operation/service-orders/v2/{id}/budget-decision`, voltado a decisao de orcamento;
- a propria descricao do endpoint orienta integracoes externas a utilizarem `/external/events`.

Isso mostra uma separacao util entre:

- API de uso direto;
- API de integracao externa orientada a eventos.

## Recursos e configuracao de runtime

Em `src/main/resources` o projeto possui:

- `application.yml`
- Liquibase em `db/changelog`
- diretorios `static/` e `templates/`

Configuracoes principais:

- PostgreSQL como banco principal;
- `ddl-auto: validate`;
- `open-in-view: false`;
- Liquibase habilitado;
- segredo JWT via variavel externa;
- Actuator expondo `health`.

Justificativa arquitetural:

- runtime sensivel a ambiente;
- configuracao externa sem hardcode de secrets;
- validacao de schema delegada ao banco e as migracoes.

## Banco e migracoes

Migracoes identificadas:

- `001-create-table-customer.sql`
- `002-create-table-vehicle.sql`
- `003-create-inventory-tables.sql`
- `004-create-service-order-and-item.sql`
- `005-create-mechanics-table.sql`
- `006-base-inserts-to-homologate.sql`

Justificativa:

- versionamento de banco no proprio repositorio;
- consistencia entre ambientes;
- evolucao controlada do schema.

## Testes

O projeto possui cobertura de testes distribuida por modulo, com 59 arquivos de teste encontrados na estrutura atual.

O foco principal esta em:

- entidades;
- casos de uso;
- mappers;
- seguranca JWT.

Justificativa arquitetural:

- a arquitetura favorece teste de unidade e teste de componente;
- os casos de uso podem ser exercitados sem depender do stack web completo.

Leitura importante:

- a maior parte do valor testavel esta no `core`, o que confirma a vantagem da separacao arquitetural adotada.

## Camada de empacotamento e execucao local

### Dockerfile

O `Dockerfile` usa build em duas etapas:

1. `maven:3.9-eclipse-temurin-21-alpine` para build;
2. `eclipse-temurin:21-jre-alpine` para runtime.

Fluxo:

- baixa dependencias;
- compila e empacota;
- publica apenas o jar final na imagem de execucao.

Justificativa:

- reduz imagem final;
- evita levar toolchain de build para producao;
- padroniza empacotamento.

### Docker Compose

O `docker-compose.yml` sobe:

- PostgreSQL 16;
- aplicacao backend;
- health check para o banco;
- injecao de variaveis de ambiente.

Justificativa:

- simplifica ambiente local;
- aproxima desenvolvimento do ambiente real;
- acelera homologacao e validacao manual.

## Camada de CI/CD

O pipeline esta em `.github/workflows/main.yaml` e possui tres jobs principais:

1. `infrastructure`
2. `build`
3. `deploy`

### Job `infrastructure`

Responsabilidades:

- checkout do codigo;
- autenticacao AWS;
- bootstrap do bucket S3 do estado remoto;
- `terraform init`;
- `terraform plan`;
- `terraform apply`;
- captura de outputs.

Outputs repassados:

- endpoint do RDS;
- URL do ECR;
- nome do cluster EKS;
- tag da imagem.

### Job `build`

Responsabilidades:

- configuracao do Java 21;
- build e testes com Maven;
- login no ECR;
- build da imagem Docker;
- push com tag do commit e `latest`.

### Job `deploy`

Responsabilidades:

- atualizacao do `kubeconfig`;
- instalacao do `metrics-server`;
- injecao de variaveis em manifestos com `envsubst`;
- apply dos manifestos Kubernetes;
- verificacao de rollout.

Justificativa arquitetural da camada de CI/CD:

- a entrega passa a ser reproducivel;
- infraestrutura e aplicacao evoluem de forma coordenada;
- o deploy deixa de depender de passos manuais dispersos.

Ponto de maturidade:

- o pipeline nao so builda a aplicacao, mas provisiona a base necessaria para executa-la;
- isso transforma o repositorio em uma unidade completa de entrega.

## Camada de IaC com Terraform

O diretorio `infra/terraform` materializa a infraestrutura como codigo.

Arquivos principais:

- `provider.tf`: provider AWS na regiao `us-east-1`;
- `backend.tf`: estado remoto em S3;
- `ecr.tf`: repositorio ECR com `scan_on_push = true`;
- `eks.tf`: cluster EKS e node group;
- `rds.tf`: banco PostgreSQL gerenciado;
- `variables.tf`: senha do banco como variavel sensivel.

### Recursos provisionados

O Terraform atualmente cobre:

- backend remoto do estado Terraform em S3;
- repositorio de imagem no ECR;
- cluster EKS;
- node group com autoscaling entre 1 e 3 nos;
- instancia RDS PostgreSQL;
- security group para o banco.

Justificativa arquitetural:

- a infraestrutura critica deixa de ser manual;
- provisionamento fica versionado e auditavel;
- ambientes podem ser reproduzidos com mais confianca.

Ponto de atencao:

- o uso de VPC default e subnets default reduz complexidade inicial, mas limita isolamento;
- o RDS esta publico e com porta `5432` aberta para `0.0.0.0/0`, o que e pratico para laboratorio, mas fragil para producao;
- o node group usa `SPOT`, o que reduz custo, mas exige tolerancia a interrupcoes.

## Clusterizacao com Kubernetes

O diretorio `infra/k8s` contem os manifestos de execucao no cluster:

- `configmap.yaml`
- `secrets.yaml`
- `deployment.yaml`
- `service.yaml`
- `hpa.yaml`

### ConfigMap

Responsavel por configuracoes nao sensiveis:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`

### Secret

Responsavel por dados sensiveis:

- `db-password`
- `jwt-secret`

### Deployment

O deployment do backend define:

- imagem vinda do ECR;
- container na porta `8080`;
- requests e limits de CPU/memoria;
- variaveis vindas de ConfigMap e Secret;
- `startupProbe`;
- `livenessProbe`;
- `readinessProbe`.

Justificativa arquitetural:

- separa configuracao da imagem;
- permite rollout seguro;
- melhora recuperacao automatica da aplicacao.

### Service

O `service.yaml` expoe a aplicacao como:

- `type: LoadBalancer`
- porta `80` para `8080`

Justificativa:

- fornece ponto de entrada estavel para acesso externo;
- desacopla consumidores do endereco dinamico dos pods.

## Alta disponibilidade e HPA

O projeto ja incorpora mecanismos importantes de disponibilidade, especialmente na camada de aplicacao.

### HPA

O `hpa.yaml` define:

- `minReplicas: 1`
- `maxReplicas: 3`
- alvo de 70 por cento de utilizacao media de CPU

### Probes

No `deployment.yaml` existem:

- `startupProbe`
- `livenessProbe`
- `readinessProbe`

Todas usando `/actuator/health`.

### Metrics Server

O pipeline instala o `metrics-server` antes de aplicar o HPA.

Isso e essencial porque:

- o HPA depende de metricas do cluster;
- sem esse componente o autoscaling horizontal nao funcionaria.

### Node Group escalavel

No Terraform, o node group do EKS esta configurado com:

- `min_size = 1`
- `desired_size = 1`
- `max_size = 3`

Leitura arquitetural:

- ha escalabilidade horizontal na camada de pods;
- ha capacidade de crescimento tambem na camada de nos do cluster.

### O que isso significa em termos de alta disponibilidade

O projeto avanca na direcao correta porque combina:

- cluster Kubernetes;
- probes para autorrecuperacao;
- service desacoplado dos pods;
- HPA para reacao a carga;
- escalonamento no node group.

Mas e importante registrar a leitura completa:

- com `replicas: 1` no baseline, a alta disponibilidade inicial e limitada ate que o HPA aumente a quantidade de pods;
- o RDS atual e uma instancia unica, e o codigo Terraform nao evidencia configuracao Multi-AZ;
- portanto, a alta disponibilidade esta mais forte na camada stateless da aplicacao do que na camada de banco.

Essa observacao nao diminui o projeto. Apenas coloca a arquitetura no nivel correto:

- boa base de resiliencia para aplicacao;
- disponibilidade ainda parcial no conjunto da plataforma.

## Justificativas para a arquitetura adotada

A arquitetura escolhida faz sentido para este projeto pelas seguintes razoes.

### 1. Crescimento funcional por dominio

O sistema lida com contextos diferentes:

- cadastro;
- inventario;
- operacao da ordem de servico;
- autenticacao.

Separar por modulo evita que tudo vire um pacote unico de controllers e services genericos.

### 2. Testabilidade

Com casos de uso no `core` e dependencias abstratas, a regra de negocio e mais facil de testar isoladamente.

### 3. Evolucao tecnologica

Persistencia, seguranca e integracoes estao nas bordas.

Isso significa que o projeto pode:

- trocar adaptadores;
- evoluir API;
- mudar detalhes de infraestrutura;

sem reescrever o coracao do negocio.

### 4. Clareza para integracoes externas

A presenca de adapters REST, presenters e webhook dedicado torna a API mais preparada para:

- consumo direto por clientes;
- automacao externa;
- integracao orientada a eventos.

### 5. Prontidao para operacao real

Nao se trata apenas de uma aplicacao de laboratorio sem esteira operacional.

O projeto inclui:

- pipeline;
- containerizacao;
- IaC;
- cluster;
- autoscaling;
- health checks.

Isso mostra uma arquitetura pensada nao apenas para codificar, mas para operar.

## Pontos fortes da revisao

- Estrutura por contexto de negocio bem definida.
- `core` claramente separado de detalhes tecnicos.
- Uso consistente de gateways, use cases e adapters.
- Presenca de presenters, o que melhora a borda de saida.
- JWT encapsulado em gateway e filtro.
- Webhook implementado de forma coerente com a arquitetura.
- Terraform, Kubernetes e GitHub Actions integrados.
- HPA e probes elevam a maturidade operacional.
- Existe base de testes relevante para o nucleo do sistema.

## Pontos de atencao identificados

- Coexistencia de `controller` e `infrastructure/web`, indicando arquitetura ainda em consolidacao na borda.
- Alguns endpoints sensiveis estao publicos no `SecurityConfig`.
- RDS publico com acesso amplo via `0.0.0.0/0`.
- Alta disponibilidade mais madura na camada de aplicacao do que na camada de banco.
- Uso de VPC default e subnets default, adequado para simplificacao, mas nao ideal para isolamento de ambientes.
- Node group com `SPOT`, o que melhora custo, mas reduz previsibilidade operacional.

## Conclusao

O `pitflow-os-backend` evoluiu de um backend Spring Boot tradicional para uma base muito mais estruturada, com boa aderencia a clean architecture e com preocupacoes reais de operacao em nuvem.

Hoje o projeto possui:

- separacao por contexto de negocio;
- dominio isolado por `core`;
- portas e adaptadores bem distribuidos;
- camada de seguranca com JWT;
- integracoes externas por webhook;
- pipeline CI/CD;
- IaC com Terraform;
- clusterizacao com Kubernetes;
- escalabilidade horizontal com HPA.

Em resumo, a arquitetura adotada e coerente porque equilibra dois objetivos ao mesmo tempo:

- preservar a regra de negocio isolada e testavel;
- preparar o sistema para execucao, deploy e escalabilidade em ambiente real.
