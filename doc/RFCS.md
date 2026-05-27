# Request for Comments (RFCs) — Pitflow OS

---
**Contribuintes:** Rafael Moreira dos Santos (Dev).

## RFC 001: Escolha do Provedor de Nuvem e Infraestrutura Base

**Data:** Maio de 2026 Status: Aceito **Status:** Aceito ✅

### Contexto

O sistema precisava de uma infraestrutura altamente escalável, resiliente e que suportasse uma arquitetura híbrida (Containers para o core business e Serverless para as bordas).

### Decisão

Utilização da **Amazon Web Services (AWS)** como provedor principal, orquestrada via **Terraform**.

### Justificativa

- **Maturidade EKS:** O Amazon EKS oferece uma gestão robusta do plano de controle do Kubernetes, essencial para a escalabilidade dos pods do Spring Boot.
- **Integração Nativa:** O AWS API Gateway tem integração nativa, segura e de baixíssima latência com o AWS Lambda e o Application Load Balancer (ALB).
- **Gestão de Segredos:** O AWS Secrets Manager provê injeção dinâmica de senhas e URLs na esteira de CI/CD (GitHub Actions) sem expor dados no repositório.
---

## RFC 002: Escolha do Banco de Dados Principal

**Data:** Maio de 2026 Status: Aceito **Status:** Aceito ✅

### Contexto

O sistema lida com o ciclo de vida de Ordens de Serviço (OS), exigindo forte consistência de dados, vínculos obrigatórios entre entidades (Cliente → Veículo → OS) e transações financeiras (Orçamentos).

### Decisão

Utilização de **PostgreSQL** gerenciado via **Amazon RDS**.

### Justificativa

- **Garantias ACID:** Consistência e integridade transacional são inegociáveis para o negócio de Ordens de Serviço e cálculos de valores.
- **Modelo Relacional Forte:** O uso de chaves estrangeiras garante que uma OS não possa existir sem um cliente ou veículo válido cadastrado.
- **Custo Operacional (RDS):** A escolha por um serviço gerenciado (RDS) elimina a complexidade de manter backups, failovers e patching manual do banco, permitindo foco no domínio do negócio.

---

## RFC 003: Estratégia de Autenticação Híbrida (Serverless + JWT)

**Data:** Maio de 2026 Status: Aceito **Status:** Aceito ✅

### Contexto

Precisávamos autenticar o cliente antes de ele interagir com o core do sistema, evitando gargalos de processamento no cluster Kubernetes principal.

### Decisão

Geração de JWT via **AWS Lambda** (`pitflow-auth`) roteado pelo API Gateway, com validação de assinatura (Interceptor) no backend EKS.

### Justificativa

- **Stateless:** O uso do JWT permite escalabilidade horizontal tanto das Lambdas quanto dos Pods do Spring Boot, sem a necessidade de manter sessões na memória (Stateful).
- **Offload de Processamento:** A validação de CPF, busca no banco e geração criptográfica do token são isoladas na Lambda, poupando a CPU do Spring Boot apenas para as regras core da Ordem de Serviço.
- **Separação de Responsabilidades:** O API Gateway atua como fronteira. Acesso não autenticado nem chega a onerar o Ingress/ALB do EKS nas rotas protegidas.
