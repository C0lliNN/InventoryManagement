module "ecs-cluster" {
  source  = "terraform-aws-modules/ecs/aws"
  version = "4.1.3"

  cluster_name = "${var.app_name}-cluster"

  cluster_configuration = {
    execute_command_configuration = {
      logging           = "OVERRIDE"
      log_configuration = {
        cloud_watch_log_group_name = "/aws/ecs/${var.app_name}"
      }
    }
  }
}