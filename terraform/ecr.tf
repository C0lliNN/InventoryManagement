resource "aws_ecr_repository" "app_repository" {
  name                 = var.app_name
  image_tag_mutability = "MUTABLE"
  force_delete         = true
}
