resource "aws_s3_bucket" "app_storage_bucket" {
  bucket        = "${var.app_name}-storage-20230127"
  force_destroy = true
}
