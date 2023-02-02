variable "github_personal_access_token" {
  type = string
  description = "GitHub token used to by terraform to perform authorized actions"
}

variable "github_repository_name" {
  type = string
  description = "Name of Github Repository where application code is stored"
}

variable "aws_access_key_id" {
  type = string
  description = "AWS ACCESS KEY ID. It's required by Github Actions to perform the deployment"
}

variable "aws_secret_access_key" {
  type = string
  description = "AWS SECRET ACCESS KEY. It's required by Github Actions to perform the deployment"
}

variable "aws_region" {
  type = string
  description = "AWS REGION. It's required by Github Actions to perform the deployment"
}

variable "ecr_repository_url" {
  type = string
  description = "Elastic Container Registry URL. It's required by Github Actions to perform the deployment"
}

variable "application_name" {
  type = string
  description = "Name of the application. It's used in a secret"
}

variable "ecs_service_name" {
  type = string
  description = "Name of the ECS Service in which the application will be deployed"
}

variable "ecs_cluster_name" {
  type = string
  description = "Name of the ECS Cluster in which the application will be deployed"
}

variable "task_definition_family" {
  type = string
  description = "ECS Task Definition Family of the application"
}