terraform {
  backend "s3" {
    bucket = "tfstate-backend-fiap-pitflow"
    key    = "infra/terraform/os-backend/terraform.tfstate"
    region = "us-east-1"
  }
}
