# Estrutura do Projeto - Pitflow OS Backend - Versao 3

## Visao Geral

O projeto `pitflow-os-backend` foi reorganizado para uma estrutura alinhada com clean architecture, mantendo a separacao por contexto de negocio e explicitando melhor as fronteiras entre dominio, casos de uso, adaptadores e infraestrutura.

Hoje o repositorio combina tres frentes:

- aplicacao Java 21 com Spring Boot 4;
- infraestrutura e deploy com Docker, Kubernetes, Terraform e GitHub Actions;
- testes automatizados focados principalmente no nucleo de negocio e nos mapeamentos.

A raiz atual do projeto e esta:

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
`-- ESTRUTURA_PROJETO_V3.md
```

## Leitura Arquitetural Atual

O codigo fonte principal fica em `src/main/java/br/com/pitflow` e foi organizado por contexto:

- `common`: componentes transversais, como seguranca, configuracao e tratamento de excecao;
- `registry`: cadastro e autenticacao de clientes, mecanicos e veiculos;
- `inventory`: catalogo de pecas e servicos;
- `operation`: ciclo de vida das ordens de servico.

Cada contexto segue uma estrutura muito parecida, com foco em separar responsabilidade:

```text
contexto/
|-- controller/
|-- core/
|   |-- entity/
|   |-- gateway/
|   |-- usecase/
|   `-- valueObject/   (quando aplicavel)
|-- infrastructure/
|   |-- config/
|   |-- persistence/
|   |-- security/      (quando aplicavel)
|   |-- notifications/ (quando aplicavel)
|   `-- web/
`-- presenter/
```

Na pratica, o fluxo dominante da aplicacao ficou assim:

```text
Entrada HTTP
  -> adapter/controller
  -> use case (core/usecase)
  -> entidade e regra de negocio (core/entity, valueObject)
  -> gateway abstrato (core/gateway)
  -> adapter de infraestrutura
  -> JPA / PostgreSQL
  -> presenter / DTO de resposta
```

## Pacote Common

O pacote `common` concentra elementos compartilhados entre os modulos:

- `common/core/gateway`
  - `PasswordVerifierGateway`
  - `TokenGateway`
- `common/infrastructure/configuration`
  - configuracao de seguranca e OpenAPI;
- `common/infrastructure/security`
  - `JwtServiceImp`
  - `SecurityFilter`;
- `common/infrastructure/exception`
  - handler centralizado de excecoes HTTP.

Esse pacote funciona como base tecnica reutilizavel pelos contextos de negocio, sem concentrar regra funcional do dominio.

## Contexto Registry

O modulo `registry` e o mais amplo em volume funcional. Ele cobre:

- clientes;
- mecanicos;
- veiculos;
- autenticacao de mecanicos.

Estrutura principal:

```text
registry/
|-- controller/
|   |-- AuthController.java
|   |-- CustomerController.java
|   |-- MechanicController.java
|   |-- VehicleController.java
|   `-- dto/
|-- core/
|   |-- entity/
|   |   |-- Customer.java
|   |   |-- Mechanic.java
|   |   `-- Vehicle.java
|   |-- gateway/
|   |-- usecase/
|   |   |-- customer/
|   |   |-- mechanic/
|   |   `-- vehicle/
|   `-- valueObject/
|       |-- CpfCnpj.java
|       `-- LicensePlate.java
|-- infrastructure/
|   |-- config/
|   |-- persistence/
|   |   |-- adapter/
|   |   |-- entity/
|   |   |-- mapper/
|   |   `-- repository/
|   |-- security/
|   `-- web/
`-- presenter/
```

Pontos importantes:

- os `inputPort` ficam dentro dos subgrupos de use case, deixando explicito o contrato de entrada de cada caso de uso;
- `outputData` aparece em autenticacao para transportar resposta de aplicacao sem acoplar o dominio ao HTTP;
- a persistencia usa adapters JPA, entidades `*Jpa`, mappers e repositos Spring Data separados;
- existem adapters de seguranca especificos para senha com bcrypt.

## Contexto Inventory

O modulo `inventory` esta focado em dois agregados: pecas e servicos.

Estrutura principal:

```text
inventory/
|-- controller/
|   |-- PartController.java
|   |-- ServiceController.java
|   `-- dto/
|-- core/
|   |-- entity/
|   |   |-- Part.java
|   |   `-- Service.java
|   |-- gateway/
|   |   |-- PartGateway.java
|   |   `-- ServiceGateway.java
|   `-- usecase/
|       |-- part/
|       `-- service/
|           `-- inputPort/
|-- infrastructure/
|   |-- config/
|   |-- persistence/
|   |   |-- adapter/
|   |   |-- entity/
|   |   |-- mapper/
|   |   `-- repository/
|   `-- web/
`-- presenter/
```

Casos de uso encontrados no modulo:

- criar;
- atualizar;
- listar;
- buscar por id;
- excluir;
- no caso de peca, buscar por SKU.

O desenho reforca uma separacao clara entre entidade de negocio, gateway abstrato e implementacao concreta de persistencia.

## Contexto Operation

O modulo `operation` concentra a orquestracao das ordens de servico.

Estrutura principal:

```text
operation/
|-- controller/
|   |-- ServiceOrderController.java
|   `-- dto/
|-- core/
|   |-- entity/
|   |   `-- ServiceOrder.java
|   |-- gateway/
|   |   |-- NotificationGateway.java
|   |   `-- ServiceOrderGateway.java
|   `-- usecase/
|       |-- inputPort/
|       `-- outputData/
|-- infrastructure/
|   |-- config/
|   |-- notifications/
|   |   `-- LogNotificationAdapter.java
|   |-- persistence/
|   |   |-- adapter/
|   |   |-- entity/
|   |   |-- mapper/
|   |   `-- repository/
|   `-- web/
`-- presenter/
```

Casos de uso implementados:

- criar ordem de servico;
- adicionar item;
- iniciar diagnostico;
- concluir diagnostico;
- aprovar;
- finalizar;
- entregar;
- cancelar;
- listar ordens;
- listar ordens em execucao;
- buscar por id;
- calcular tempo medio de execucao;
- calcular duracao de ordem.

Esse modulo e o que mais evidencia o uso de clean architecture, porque combina:

- regra de negocio no `core`;
- contratos de entrada em `inputPort`;
- contratos de saida de infraestrutura em `gateway`;
- adaptador de notificacao desacoplado;
- modelos especificos de saida em `outputData`.

## Controller x Infrastructure/Web

A estrutura atual mostra dois pontos de entrada relacionados a API:

- `controller/` com classes `*Controller` e `dto/*Command`;
- `infrastructure/web/` com classes `*RestAdapter` e `dto/*Request`.

Isso indica que a refatoracao ainda convive com duas convencoes de borda:

- uma mais proxima da estrutura anterior, baseada em controllers;
- outra mais alinhada ao vocabulario de adapters HTTP da clean architecture.

Como leitura arquitetural, o repositorio ja caminha para um modelo de adapters de entrada, mas ainda preserva parte da camada anterior. Vale tratar isso no projeto como um estado de transicao, nao como duplicacao acidental sem contexto.

## Presenter

Todos os modulos de negocio possuem `presenter/`, o que e um sinal importante da refatoracao.

Responsabilidades observadas:

- transformar entidades e modelos de aplicacao em respostas publicas;
- isolar DTOs de saida em `presenter/dto`;
- reduzir acoplamento entre dominio e serializacao HTTP.

Exemplos:

- `registry/presenter/CustomerPresenter.java`
- `inventory/presenter/PartPresenter.java`
- `operation/presenter` com DTOs proprios de resposta para ordem de servico.

## Persistencia

Os tres contextos de negocio seguem o mesmo padrao na persistencia:

```text
infrastructure/persistence/
|-- adapter/
|-- entity/
|-- mapper/
`-- repository/
```

Separacao de responsabilidades:

- `adapter/`: implementa os gateways definidos no `core`;
- `entity/`: representa o modelo JPA;
- `mapper/`: converte entre modelo de dominio e modelo JPA;
- `repository/`: interfaces Spring Data.

Esse padrao e consistente com clean architecture, porque o dominio depende apenas de contratos, e nao da tecnologia de persistencia.

## Resources e Banco

O diretorio `src/main/resources` contem:

- `application.yml`;
- `db/changelog/db.changelog-master.yaml`;
- scripts Liquibase em `db/changelog/migrations`;
- diretorios `static/` e `templates/`.

Migracoes identificadas:

- `001-create-table-customer.sql`
- `002-create-table-vehicle.sql`
- `003-create-inventory-tables.sql`
- `004-create-service-order-and-item.sql`
- `005-create-mechanics-table.sql`
- `006-base-inserts-to-homologate.sql`

Configuracao principal da aplicacao:

- PostgreSQL como banco principal;
- `ddl-auto: validate`;
- `open-in-view: false`;
- Liquibase habilitado;
- JWT por `JWT_SECRET`;
- actuator expondo `health`.

## Testes

O projeto possui uma base relevante de testes em `src/test/java/br/com/pitflow`, espelhando a divisao dos modulos:

- `common`;
- `inventory`;
- `operation`;
- `registry`.

O foco atual dos testes esta em:

- entidades do dominio;
- casos de uso;
- mappers de persistencia;
- componentes de seguranca JWT.

Tambem existe `src/test/resources/application.yml`, indicando configuracao de teste separada do ambiente principal.

## Infraestrutura Fora do Codigo de Negocio

Mesmo com a refatoracao interna da aplicacao, o repositorio continua com a camada operacional completa:

- `Dockerfile` para build multi-stage;
- `docker-compose.yml` para ambiente local;
- `.github/workflows/main.yaml` para pipeline;
- `infra/k8s` com deployment da aplicacao;
- `infra/terraform` com provisionamento AWS.

Ou seja, a clean architecture foi aplicada principalmente no codigo da aplicacao, enquanto a parte de entrega e execucao permaneceu centralizada no mesmo repositorio.

## Resumo Estrutural

Na versao atual, a organizacao do projeto pode ser lida assim:

1. `core` concentra regra de negocio, contratos e casos de uso.
2. `infrastructure` implementa persistencia, seguranca, notificacao e web adapters.
3. `presenter` organiza a saida da aplicacao para consumo externo.
4. `controller` ainda existe como camada de entrada, sugerindo transicao entre o desenho antigo e o novo.
5. `common` concentra capacidades transversais reutilizadas pelos modulos.

Em comparacao com a estrutura anterior, a principal mudanca nao foi apenas reorganizar pastas, mas explicitar dependencias arquiteturais:

- o dominio passou a depender de gateways e portas;
- os adapters concretos foram empurrados para a infraestrutura;
- a resposta da aplicacao ganhou uma camada propria com presenters;
- os contextos de negocio ficaram mais simetricos entre si.

## Conclusao

Partindo da raiz real do repositorio, a versao 3 do projeto mostra uma aplicacao modularizada por contexto de negocio e por responsabilidade tecnica. O resultado e uma base mais proxima de clean architecture, com `core` isolado, gateways abstratos, adapters de persistencia e web, presenters de saida e testes cobrindo o nucleo funcional.

O ponto mais importante da leitura atual e este: a refatoracao ja estabeleceu a espinha dorsal da nova arquitetura, mas a coexistencia de `controller/` e `infrastructure/web/` mostra que a migracao ainda nao esta 100 por cento consolidada em uma unica convencao de entrada HTTP.
