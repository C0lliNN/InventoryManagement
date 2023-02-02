terraform {
  required_providers {
    github = {
      source  = "integrations/github"
      version = "~> 5.0"
    }
  }
}


resource "github_actions_secret" "aws_access_key_id" {
  repository      = var.github_repository_name
  secret_name     = "AWS_ACCESS_KEY_ID"
  plaintext_value = var.aws_access_key_id
}

resource "github_actions_secret" "aws_secret_access_key" {
  repository      = var.github_repository_name
  secret_name     = "AWS_SECRET_ACCESS_KEY"
  plaintext_value = var.aws_secret_access_key
}

resource "github_actions_secret" "aws_region" {
  repository      = var.github_repository_name
  secret_name     = "AWS_REGION"
  plaintext_value = var.aws_region
}

resource "github_actions_secret" "ecr_repository" {
  repository      = var.github_repository_name
  secret_name     = "ECR_REPOSITORY"
  plaintext_value = var.ecr_repository_url
}

resource "github_actions_secret" "app_name" {
  repository      = var.github_repository_name
  secret_name     = "APP_NAME"
  plaintext_value = var.application_name
}

resource "github_actions_secret" "ecs_service_name" {
  repository      = var.github_repository_name
  secret_name     = "ECS_SERVICE"
  plaintext_value = var.ecs_service_name
}

resource "github_actions_secret" "task_definition_family" {
  repository      = var.github_repository_name
  secret_name     = "TASK_DEFINITION_FAMILY"
  plaintext_value = var.task_definition_family
}

resource "github_actions_secret" "ecs_cluster_name" {
  repository      = var.github_repository_name
  secret_name     = "ECS_CLUSTER"
  plaintext_value = var.ecs_cluster_name
}

