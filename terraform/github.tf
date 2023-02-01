resource "github_repository" "InventoryManagement" {
  name          = "InventoryManagement"
  description   = "A Non-Blocking REST API developed in Java 11, Spring Boot 2 and Spring WebFlux to manage the Inventory of an Industry."
  topics        = ["java", "spring-boot", "webflux"]
  has_downloads = true
  has_issues    = true
  has_projects  = true
  has_wiki      = true
  homepage_url  = "${aws_alb.app_alb.dns_name}/docs"
}

resource "github_actions_secret" "aws-access-key-id" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "AWS_ACCESS_KEY_ID"
  encrypted_value = var.aws_access_key_id
}

resource "github_actions_secret" "aws-secret-access-key" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "AWS_SECRET_ACCESS_KEY"
  encrypted_value = var.aws_secret_access_key
}

resource "github_actions_secret" "aws-region" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "AWS_REGION"
  plaintext_value = var.region
}

resource "github_actions_secret" "ecr-repository" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "ECR_REPOSITORY"
  plaintext_value = aws_ecr_repository.app_repository.name
}

resource "github_actions_secret" "app-name" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "APP_NAME"
  plaintext_value = var.app_name
}

resource "github_actions_secret" "ecs-service-name" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "ECS_SERVICE"
  plaintext_value = aws_ecs_service.inventory-management-service.name
}

resource "github_actions_secret" "task-definition-family" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "TASK_DEFINITION_FAMILY"
  plaintext_value = aws_ecs_task_definition.task-definition.family
}

resource "github_actions_secret" "ecs-cluster-name" {
  repository      = github_repository.InventoryManagement.id
  secret_name     = "ECS_CLUSTER"
  plaintext_value = module.ecs-cluster.cluster_name
}

