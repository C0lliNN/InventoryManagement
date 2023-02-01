resource "github_actions_secret" "aws-access-key-id" {
  repository      = var.github_repository_name
  secret_name     = "AWS_ACCESS_KEY_ID"
  plaintext_value = var.aws_access_key_id
}

resource "github_actions_secret" "aws-secret-access-key" {
  repository      = var.github_repository_name
  secret_name     = "AWS_SECRET_ACCESS_KEY"
  plaintext_value = var.aws_secret_access_key
}

resource "github_actions_secret" "aws-region" {
  repository      = var.github_repository_name
  secret_name     = "AWS_REGION"
  plaintext_value = var.region
}

resource "github_actions_secret" "ecr-repository" {
  repository      = var.github_repository_name
  secret_name     = "ECR_REPOSITORY"
  plaintext_value = aws_ecr_repository.app_repository.name
}

resource "github_actions_secret" "app-name" {
  repository      = var.github_repository_name
  secret_name     = "APP_NAME"
  plaintext_value = var.app_name
}

resource "github_actions_secret" "ecs-service-name" {
  repository      = var.github_repository_name
  secret_name     = "ECS_SERVICE"
  plaintext_value = aws_ecs_service.inventory-management-service.name
}

resource "github_actions_secret" "task-definition-family" {
  repository      = var.github_repository_name
  secret_name     = "TASK_DEFINITION_FAMILY"
  plaintext_value = aws_ecs_task_definition.task-definition.family
}

resource "github_actions_secret" "ecs-cluster-name" {
  repository      = var.github_repository_name
  secret_name     = "ECS_CLUSTER"
  plaintext_value = module.ecs-cluster.cluster_name
}

