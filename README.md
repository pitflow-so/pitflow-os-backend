# PitFlow OS - Backend 🛠️ (Fase 3)

Aplicação backend orientada a domínio (DDD), baseada em Clean Architecture, executando em ambiente cloud-native (Kubernetes), com infraestrutura como código (Terraform) e pipeline CI/CD automatizado.

📌 **Links Importantes:**
* 🎬 **Vídeo Demonstrativo:** [Link do Vídeo](**https://drive.google.com/drive/folders/1rKD-R3Cbbh3VkMbq_KHJeAC3ShDwRWPx?usp=sharing**).
* 📚 **Collection / Swagger API:** A documentação interativa (OpenAPI) fica disponível em `http://localhost:8080/swagger-ui.html` quando a aplicação está em execução, localmente.

---
## ⚒️ Requisitos
* **Java 21** (openjdk 21.0.2)
* **Docker/Docker-compose** ( version 29.1.4-rd)
* **aws cli** (aws-cli/2.34.11)
* **Terraform** (v1.14.7)

## 🏗️ 1. Arquitetura (Clean Architecture)

A aplicação foi completamente refatorada seguindo os princípios da **[Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)** (Arquitetura Limpa), garantindo que as regras de negócio sejam o coração do sistema, independentes de frameworks, bancos de dados ou interfaces web.

### Organização em Módulos
O código está estruturado em três Bounded Contexts principais:
1. **`common`**: Elementos transversais (Filtros JWT, Gateways abstratos como `TransactionGateway`, Handlers de exceção).
2. **`inventory`**: Catálogo de peças e serviços.
3. **`operation`**: Máquina de estados e ciclo de vida das Ordens de Serviço (Abertura, Diagnóstico, Aprovação, Execução e Finalização).

O contexto de clientes, veículos e mecânicos foi extraído para o repositório
`pitflow-registry`. O contexto `operation` acessa esse serviço por um gateway
HTTP interno.

## 🧠 Decisões Arquiteturais

- Separação entre Controller (Application) e REST Adapter (Infrastructure)
- Uso de Gateways para inversão de dependência.
- DTOs para isolamento e controle de contexto no transporte de dados.
- Kubernetes para escalabilidade horizontal.

### O Fluxo de Dependência
A regra de dependência aponta sempre para o centro (`core`):
* **Core:** Contém *Entities*, *Value Objects* e *Use Cases* puros (Java puro, sem anotações de Spring).
* **Infrastructure:** Contém os *Adapters* que implementam as interfaces (Gateways) do Core. Aqui residem a implementação de Frameworks e Drivers, como as lógicas de JPA, Security, Webhooks, REST e mapeamento relacional.
* **Controllers:** Responsáveis por receber a entrada, transformar em comandos e delegar aos casos de uso.
* **Presenters:** Responsáveis por formatar a saída (DTOs) para o mundo externo. <br>

![img.png](doc/img/components_aplicacao.png)

**Exemplo de caso de uso:**

```mermaid
%%{init: { "theme": "base" }}%%
flowchart LR
    subgraph Frameworks ["Frameworks / Drivers Layer"]
        REST[ServiceOrderRestAdapter]
    end

    subgraph Interfaces ["Interfaces Adapters Layer"]
        CTRL[ServiceOrderController]
    end

    subgraph Core ["Core Layer"]
        USECASE["Use Cases<br/>Create / Approve / Cancel / etc"]
        ENTITY[ServiceOrder Entity]
        GATEWAY["ServiceOrderGateway (interface)"]
    end

    subgraph Infrastructure ["Infrastructure Layer"]
        JPA[JpaServiceOrderGatewayAdapter]
        REPO["Spring Data Repository"]
        DB[(PostgreSQL)]
    end

    REST --> CTRL
    CTRL --> USECASE
    USECASE --> ENTITY
    USECASE --> GATEWAY
    GATEWAY --> JPA
    JPA --> REPO
    REPO --> DB
```

---

## ☁️ 2. Infraestrutura e Orquestração (Cloud & IaC)

O projeto foi modernizado para rodar em um ambiente escalável na **AWS**.

### Infraestrutura como Código (Terraform)
Localizado na pasta `infra/terraform`, o IaC provisiona:
* **Amazon EKS:** Cluster Kubernetes e Node Group (capacidade SPOT).
* **Amazon RDS:** Banco PostgreSQL 16 gerenciado.
* **Amazon ECR:** Repositório privado de imagens Docker.
* **Amazon S3:** Backend remoto para guardar o estado do Terraform (`tfstate`).

### Orquestração (Kubernetes)
Localizado na pasta `infra/k8s`, os manifestos definem a topologia:
* **Deployment:** Responsável por manter os pods da aplicação.
* **Service:** Exposição da aplicação via Service do tipo LoadBalancer.
* **ConfigMaps e Secrets:** Injeção de variáveis de ambiente (`DB_HOST`, senhas e JWT) desacopladas da imagem da aplicação.
* **Probes (Liveness/Readiness/Startup):** Garantem a autorrecuperação dos pods usando o Spring Actuator (`/actuator/health`).
* **HPA (Horizontal Pod Autoscaler):** Escalonamento automático de 1 para até 3 réplicas com base no consumo de CPU (alvo: 70%).

Segue a representação da estrutura:
```mermaid
%%{init: { "theme": "base" }}%%
flowchart TB

    %% ===== AWS =====
    subgraph AWS["AWS Cloud"]
        S3[(S3 - Terraform State)]
        ECR[(ECR - Docker Images)]
        EKS[(EKS - Kubernetes Cluster)]
        RDS[(RDS - PostgreSQL)]
    end

    %% ===== KUBERNETES =====
    subgraph Kubernetes["Kubernetes Cluster (EKS)"]
        POD[Spring Boot Pod]
        SVC[Service]
        HPA[Horizontal Pod Autoscaler]
        CM[ConfigMap]
        SECRET[Secrets]
    end

    %% ===== RELAÇÕES =====
    ECR --> POD
    POD --> RDS

    CM --> POD
    SECRET --> POD

    POD --> SVC
    SVC --> HPA

    EKS --> POD

    %% ===== ESTILOS =====
    
    %% AWS
    style S3 fill:#4CAF50,stroke:#2E7D32,color:#ffffff
    style ECR fill:#9E9E9E,stroke:#616161,color:#ffffff
    style EKS fill:#607D8B,stroke:#37474F,color:#ffffff
    style RDS fill:#FF9800,stroke:#E65100,color:#ffffff

    %% Kubernetes
    style POD fill:#2196F3,stroke:#0D47A1,color:#ffffff
    style SVC fill:#64B5F6,stroke:#1976D2,color:#000000
    style HPA fill:#BBDEFB,stroke:#1976D2,color:#000000
    style CM fill:#FFF176,stroke:#FBC02D,color:#000000
    style SECRET fill:#F06292,stroke:#AD1457,color:#ffffff
```

---

## 🚀 3. Pipeline CI/CD (GitHub Actions)

A esteira de entrega contínua (`.github/workflows/main.yaml`) automatiza todo o
processo em três jobs sequenciais:

1. **Fetch Secrets:** Carrega as secrets necessárias para o projeto. Uliza `aws secretsmanager` para obter os dados do secret manager. Exporta como variáveis para outputs para serem utilizadas nos jobs seguintes.

2. **Build and Push:** Executa `mvn clean package` (compila, testa e empacota),
   autentica no Amazon ECR e envia a imagem Docker com duas tags: o SHA do commit
   e `latest`.

3. **Deploy:** Atualiza o kubeconfig do EKS, instala o
   `metrics-server` (pré-requisito do HPA), substitui os placeholders dos
   manifestos via `envsubst` injetando secrets do GitHub, aplica todos os
   manifestos e valida o rollout do deployment. No final é atualizado o secret manager com a URL do loadbalace (`LB_URL`).
```mermaid
%%{init: {
  "theme": "base",
  "themeVariables": {
    "primaryTextColor": "#000000",
    "secondaryTextColor": "#000000",
    "tertiaryTextColor": "#000000",
    "lineColor": "#333333",
    "fontSize": "14px"
  }
}}%%
sequenceDiagram
    participant Dev as Developer
    participant GH as GitHub Actions
    participant ASM as Amazon Secret Manager
    participant ECR as Amazon ECR
    participant EKS as Amazon EKS

    Dev->>GH: push na main

    rect rgb(220, 235, 255)
        note over GH,ASM: Job 1 — Fetch Secrets
        GH->>GH: Configure AWS Credentials
        GH->>GH: Install jq
        GH->>ASM: Busca secrets e enviar para GITHUB_OUTPUT
        GH->>GH: E enviar para GITHUB_OUTPUT
    end

    rect rgb(220, 255, 220)
        note over GH,ECR: Job 2 — Build and Push
        GH->>GH: mvn clean package
        GH->>ECR: docker build + push
    end

    rect rgb(255, 235, 220)
        note over GH,EKS: Job 3 — Deploy
        GH->>EKS: update kubeconfig
        GH->>EKS: apply manifests (envsubst)
        GH->>ASM: Atualiza loadbalance URL
        EKS-->>GH: rollout OK
    end
```
---

## 📦 4. Como Executar o Projeto

Para o histórico de testes de qualidade (JaCoCo, OWASP Dependency-Check) e o teste prático de escalabilidade do HPA, consulte a pasta `/doc`.

### Opção A: Execução Local (Docker Compose)
A maneira mais rápida de rodar o ambiente integrado de desenvolvimento:

1. Mantenha os repositórios `pitflow-os-backend` e `pitflow-registry` no mesmo
   diretório pai.
2. Renomeie o arquivo `.env.example` para `.env`, se quiser sobrescrever os
   valores locais. O Compose possui valores padrão para desenvolvimento.
3. Na raiz do `pitflow-os-backend`, execute:

   ```bash
   docker-compose up --build
   ```

O ambiente cria quatro containers:

- backend em `http://localhost:18080`;
- registry em `http://localhost:18081`;
- PostgreSQL exclusivo do backend;
- PostgreSQL exclusivo do registry.

Os bancos não publicam portas no host. A comunicação do backend com o registry
usa `http://registry:8080` dentro da rede do Compose, simulando o DNS
`http://pitflow-registry` utilizado no Kubernetes.

Health checks:

```text
http://localhost:18080/actuator/health
http://localhost:18081/actuator/health
```

Para encerrar o ambiente preservando os bancos:

```bash
docker-compose down
```

Para recriar todo o ambiente e apagar os dados locais:

```bash
docker-compose down --volumes --remove-orphans
```

### Opção B: Provisionamento Terraform Local
O deploy principal é automatizado pela GitHub Action ao realizar um push na `main`. <br>
Código da action disponível em: <br>

👉 doc/[EXECUCAO_TERRAFORM_LOCAL.md](doc/EXECUCAO_TERRAFORM_LOCAL.md).

### Opção C: Deploy no Kubernetes
⚠️ **Atenção:** Para o correto funcionamento do fluxo de CI/CD no GitHub Actions, é estritamente necessário configurar as seguintes *Secrets* no repositório:
* AWS_ACCESS_KEY_ID
* AWS_SECRET_ACCESS_KEY
* AWS_SESSION_TOKEN
* DB_PASSWORD
* JWT_SECRET

Execução da action disponibilizada em: [github/workflows/main.yaml](.github/workflows/main.yaml). <br>

### Testes unitários
````bash
mvn clean test
````

### 📊 Documentos
A documentação detalhada das provas de conceito e histórico de qualidade encontra-se na pasta `/doc`:
* 🛢️ **Justificativa para o uso do banco de dados PostegreSQL:** [JUSTIFICATIVA_BANCO_DE_DADOS.md](doc/JUSTIFICATIVA_BANCO_DE_DADOS.md)
* 📦 **Diagrama de componentes:** [COMPONENTES.md](doc/COMPONENTES.md)
* 📉 **Diagrama de sequência fluxo de criação de OS** [DIAGRAMA_SEQUENCIA.md](doc/DIAGRAMA_SEQUENCIA.md)
* 📌 **RFCs** [RFCS.md](doc/RFCS.md)
* 📍 **ADRs** [ADRS.md](doc/ADRS.md)
* 🧪 **Roteiro de Homologação (MVP):** [HOMOLOGACAO.md](doc/HOMOLOGACAO.md)
* 📈 **Teste de Escalonamento Automático (HPA):** [TESTE_HPA.md](doc/TESTE_HPA.md)
* 🛡️ **Qualidade e Cobertura (JaCoCo):** [QUALIDADE_SEGURANCA.md](doc/QUALIDADE_SEGURANCA.md)
* 🔒 **Análise de Vulnerabilidades (OWASP):** [OWASP.md](doc/OWASP.md)
