# Estrutura do Projeto - Pitflow OS Backend - Versão 2

## Visão Geral

O projeto `pitflow-os-backend` é um backend Java 21 com Spring Boot voltado à gestão de oficinas, organizado em camadas inspiradas em DDD e complementado por uma esteira de infraestrutura e deploy em AWS.

Além da aplicação principal, o repositório agora explicita três frentes operacionais:

- empacotamento e execução local com `Dockerfile` e `docker-compose.yml`;
- automação de provisionamento, build e deploy com `.github/workflows/main.yaml`;
- infraestrutura como código em `infra/terraform` e manifestos Kubernetes em `infra/k8s`.

## Estrutura Resumida

```text
pitflow-os-backend/
|-- .github/
|   `-- workflows/
|       `-- main.yaml
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
|   |   |   |-- PitflowOsBackendApplication.java
|   |   |   |-- common/
|   |   |   |-- inventory/
|   |   |   |-- operation/
|   |   |   `-- registry/
|   |   `-- resources/
|   |       |-- application.yml
|   |       `-- db/changelog/
|   `-- test/
|       `-- java/br/com/pitflow/
|-- Dockerfile
|-- docker-compose.yml
|-- pom.xml
|-- README.md
|-- HOMOLOGACAO.md
`-- ESTRUTURA_PROJETO.md
```

## Núcleo da Aplicação

O backend continua dividido em quatro agrupamentos principais:

- `common`: segurança JWT, configurações, filtros, objetos de valor e componentes compartilhados.
- `registry`: cadastro de clientes, veículos e mecânicos.
- `operation`: abertura e evolução do ciclo de vida das ordens de serviço.
- `inventory`: catálogo de peças, serviços e controle de estoque.

O fluxo arquitetural dominante segue este encadeamento:

```text
Controller REST
  -> Caso de Uso (application)
  -> Entidade / Regra de Negócio (domain)
  -> Interface de Repositório (domain)
  -> Adapter de Persistência (infrastructure)
  -> Spring Data / JPA / PostgreSQL
```

## Configuração da Aplicação

O arquivo `src/main/resources/application.yml` mostra que a aplicação está preparada para receber parâmetros externos de runtime:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e `DB_PASSWORD` para conexão com PostgreSQL;
- `JWT_SECRET` para assinatura e validação de tokens;
- endpoint `/actuator/health` exposto para health checks;
- `Liquibase` habilitado para migração de banco.

Essa configuração conecta corretamente o backend tanto ao `docker-compose` local quanto ao deployment em Kubernetes.

## Containerização

### Dockerfile

O `Dockerfile` usa estratégia de multi-stage build:

1. imagem `maven:3.9-eclipse-temurin-21-alpine` para build;
2. download antecipado de dependências com `mvn dependency:go-offline`;
3. empacotamento com `mvn clean package -DskipTests`;
4. imagem final enxuta com `eclipse-temurin:21-jre-alpine`;
5. publicação da aplicação na porta `8080`.

Esse desenho reduz o tamanho da imagem final e evita levar o toolchain de build para produção.

### Docker Compose

O `docker-compose.yml` monta um ambiente local com dois serviços:

- `db`: PostgreSQL 16 Alpine com volume persistente e healthcheck;
- `app`: backend construído a partir do `Dockerfile`, dependente do banco saudável.

O compose também injeta as variáveis de ambiente necessárias para o backend:

- `DB_HOST=db`
- `DB_PORT=5432`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`

Na prática, ele oferece um fluxo local simples para subir a API junto com o banco de dados.

## Pipeline GitHub Actions

O arquivo `.github/workflows/main.yaml` descreve uma pipeline completa chamada `Pitflow Full Pipeline`, acionada em:

- `push` para a branch `main`;
- execução manual com `workflow_dispatch`.

Ela está dividida em três jobs.

### 1. `infrastructure`

Responsável por provisionar a base AWS:

- faz checkout do código;
- configura credenciais AWS;
- garante a existência do bucket S3 para o estado remoto do Terraform;
- executa `terraform init`, `terraform plan` e `terraform apply`;
- captura outputs do Terraform para os próximos jobs.

Outputs repassados:

- `rds_endpoint`
- `ecr_url`
- `eks_cluster_name`
- `image_tag`

### 2. `build`

Responsável por compilar, testar e publicar a imagem:

- configura Java 21;
- executa `mvn clean package`;
- autentica no Amazon ECR;
- gera e envia a imagem Docker com duas tags:
  - `${github.sha}`
  - `latest`

Esse job usa o repositório ECR criado pelo Terraform no job anterior.

### 3. `deploy`

Responsável por aplicar a carga de trabalho no cluster:

- atualiza o `kubeconfig` do EKS;
- instala o `metrics-server`;
- substitui placeholders dos manifestos com `envsubst`;
- aplica `ConfigMap`, `Secret`, `Deployment`, `Service` e `HPA`;
- valida o rollout do deployment `pitflow-backend`.

Esse job consolida o fluxo de entrega contínua da aplicação já empacotada.

## Infraestrutura Kubernetes

O diretório `infra/k8s` contém os manifestos da aplicação no cluster.

### `configmap.yaml`

Centraliza configuração não sensível:

- `DB_HOST` vindo do endpoint do RDS;
- `DB_PORT=5432`;
- `DB_NAME=pitflow_os`;
- `DB_USER=pitflow`.

### `secrets.yaml`

Armazena configuração sensível injetada pela pipeline:

- `db-password`
- `jwt-secret`

### `deployment.yaml`

Define a execução do backend no cluster:

- deployment `pitflow-backend`;
- imagem recebida dinamicamente via `${CONTAINER_IMAGE}`;
- porta `8080`;
- requests e limits de CPU/memória;
- variáveis vindas de `ConfigMap` e `Secret`;
- `startupProbe`, `livenessProbe` e `readinessProbe` usando `/actuator/health`.

Esse manifesto mostra uma preocupação operacional maior do que a versão original do projeto, porque já inclui observabilidade básica e readiness para rollout.

### `service.yaml`

Expõe a aplicação internamente e externamente:

- service `pitflow-service`;
- porta `80` apontando para `8080`;
- tipo `LoadBalancer`.

### `hpa.yaml`

Habilita escalabilidade horizontal:

- mínimo de 1 réplica;
- máximo de 3 réplicas;
- gatilho baseado em uso médio de CPU de 70%.

Como o HPA depende de métricas, isso explica a instalação do `metrics-server` dentro da pipeline.

## Infraestrutura Terraform

O diretório `infra/terraform` define os recursos AWS.

### `provider.tf`

Configura o provider AWS na região `us-east-1`.

### `backend.tf`

Define backend remoto do Terraform em S3:

- bucket `tfstate-backend-fiap-pitflow`;
- chave `infra/terraform/terraform.tfstate`;
- região `us-east-1`.

### `ecr.tf`

Cria o repositório de imagens:

- `aws_ecr_repository.backend`
- nome `pitflow-os-backend`
- `scan_on_push = true`
- tag mutável

Também exporta `ecr_repository_url`.

### `eks.tf`

Cria a base computacional para o Kubernetes:

- cluster EKS `pitflow-eks`;
- uso da role `LabRole`;
- uso da VPC default e subnets default filtradas por zonas;
- node group `pitflow-node-group`;
- escalonamento entre 1 e 3 nós;
- instâncias `t3.medium`;
- capacidade `SPOT`.

Também exporta `eks_cluster_name`.

### `rds.tf`

Cria o banco PostgreSQL gerenciado:

- security group liberando `5432`;
- instância `postgres` versão 16;
- database `pitflow_os`;
- usuário `pitflow`;
- `publicly_accessible = true`;
- sem proteção contra deleção;
- sem snapshot final.

Também exporta `rds_endpoint`.

### `variables.tf` e `terraform.tfvars`

Definem a variável sensível `db_password`.

## Fluxo Operacional de Ponta a Ponta

O fluxo consolidado do repositório pode ser entendido assim:

```text
Push na main
  -> GitHub Actions
  -> Terraform provisiona S3, ECR, EKS e RDS
  -> Maven compila e testa a aplicação
  -> Docker builda a imagem
  -> Imagem é enviada ao ECR
  -> Pipeline injeta variáveis nos manifestos
  -> Kubernetes aplica Deployment, Service e HPA
  -> Aplicação sobe no EKS conectada ao PostgreSQL RDS
```

## Leitura Arquitetural Atual

Com a inclusão de `.github` e `infra`, o projeto deixa de ser apenas um backend Spring Boot com banco relacional e passa a ter quatro camadas claramente visíveis:

1. camada de domínio e aplicação;
2. camada de persistência e segurança;
3. camada de empacotamento e execução local com Docker;
4. camada de provisionamento e entrega contínua em AWS/Kubernetes.

Isso mostra uma evolução relevante em maturidade operacional, porque a infraestrutura necessária para executar a solução está versionada no mesmo repositório.

## Observações Importantes

- O `docker-compose.yml` atende o cenário local e usa o mesmo conjunto de variáveis que a aplicação espera em produção.
- O `deployment.yaml` depende de `envsubst`, portanto a substituição dos placeholders acontece na pipeline e não no cluster.
- O `metrics-server` é pré-requisito direto do `hpa.yaml`.
- O `RDS` está configurado como público e com `5432` aberto para `0.0.0.0/0`, o que simplifica testes, mas representa uma decisão de segurança relevante.
- O Terraform usa VPC e subnets default, o que reduz complexidade inicial, mas limita o isolamento de ambientes.
- A pipeline une provisionamento e deploy no mesmo fluxo. Isso é prático para laboratório e MVP, mas aumenta acoplamento entre mudança de aplicação e mudança de infraestrutura.

## Resumo Final

Na versão 2, a estrutura do projeto pode ser lida como um backend DDD em Spring Boot com suporte completo a execução local e deploy em nuvem:

- `src/` representa o produto de negócio;
- `Dockerfile` e `docker-compose.yml` representam o ambiente local e o empacotamento;
- `.github/workflows/main.yaml` representa a orquestração CI/CD;
- `infra/terraform` representa o provisionamento da AWS;
- `infra/k8s` representa a publicação da aplicação no cluster.

O repositório, portanto, reúne aplicação, infraestrutura e automação de entrega dentro de uma única base versionada.
