# Guia de Provisionamento Manual (Terraform Local)

O repositório foi projetado para que o GitHub Actions gerencie a infraestrutura AWS via Terraform. <br>
👣 Para executar o terraform de forma local siga os passos abaixos.

## Pré-requisitos
* **AWS CLI** instalado e configurado (`aws configure`) com acesso à conta AWS (ou credenciais de Learner Lab ativas).
* **Terraform CLI** instalado (versão 1.5+).

## Passo 1: Ajustar o Backend de Estado (S3)
Na pipeline automatizada, o estado (`.tfstate`) é guardado em um bucket S3. Se o bucket ainda não existe na sua conta, o Terraform local falhará ao tentar inicializar. <br>
* Abra o arquivo `infra/terraform/backend.tf`.
* **Comente todo o bloco** para usar o estado local.
```bash
#terraform {
#  backend "s3" {
#    bucket = "tfstate-backend-fiap-pitflow"
#    key    = "infra/terraform/terraform.tfstate"
#    region = "us-east-1"
#  }
#}
```

## Passo 2: Criar o arquivo de variáveis sensíveis
O Terraform exige a senha do banco de dados. <br>
Na pasta `infra/terraform`, crie um arquivo chamado `terraform.tfvars` e insira o seguinte conteúdo:
```hcl
db_password = "senha_secreta"
```

## Passo 3: Inicializar e Aplicar
1. Abra o terminal na pasta `infra/terraform` e execute:

```bash
terraform init
```
2. Validar o que será criado:

```bash
terraform plan
```
3. Provisionar a infraestrutura (S3, ECR, EKS, RDS)
   Responda yes quando solicitado.
```bash
terraform apply
```
Responda `yes` quando solicitado. <br>
⚠️ **Atenção**: O provisionamento de um banco de dados RDS e de um cluster EKS pode levar cerca de 15 a 20 minutos. Não interrompa o processo.

### Passo 4: Destruir (Clean up)
Para evitar custos ou consumo dos créditos do AWS Academy, destrua os recursos após a validação:
```bash
terraform destroy
```
