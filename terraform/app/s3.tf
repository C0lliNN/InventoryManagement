resource "aws_s3_bucket" "file-storage" {
  bucket        = "${var.app_name}-storage-20230127"
  force_destroy = true
}
