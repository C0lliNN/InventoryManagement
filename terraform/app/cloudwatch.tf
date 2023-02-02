resource "aws_cloudwatch_log_group" "log_group" {
  name = "apps/${var.app_name}"
}
