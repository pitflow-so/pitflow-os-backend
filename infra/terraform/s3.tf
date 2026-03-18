resource "aws_s3_bucket" "bucket-backend" {
  bucket = "tfstate-backend-fiap-pitflow"
  force_destroy = true

  tags = {
    Name        = "tfstate"
    Environment = "Production"
  }
}