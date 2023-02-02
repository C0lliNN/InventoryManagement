terraform {
  required_providers {
    mongodbatlas = {
      source = "mongodb/mongodbatlas"
      version = "= 1.8.0"
    }

    github = {
      source  = "integrations/github"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region  = var.region
  access_key = var.aws_access_key_id
  secret_key = var.aws_secret_access_key
}

provider "mongodbatlas" {
  public_key = var.mongodbatlas_public_key
  private_key  = var.mongodbatlas_private_key
}

provider "github" {
  token = var.github_personal_access_token
}

locals {
  mongodb_connection_string = "mongodb+srv://${var.db_username}:${var.db_password}@${split("//", module.mongodb_cluster.mongodb_cluster_uri)[1]}/${var.app_name}"
}

module "mongodb_cluster" {
  source = "./mongodb"

  cluster_name      = "${var.app_name}-cluster"
  database_name     = var.app_name
  database_password = var.db_password
  database_username = var.db_username
  mongo_org_id      = var.mongo_org_id
  project_name      = var.app_name
  region            = replace(upper(var.region), "-", "_")
}

module "app" {
  source = "./app"

  app_name                           = var.app_name
  mongodb_connection_string = local.mongodb_connection_string
  region                             = var.region

  depends_on = [module.mongodb_cluster]
}

module "github_repository_configuration" {
  source = "./github"

  application_name             = var.app_name
  aws_region                   = var.region
  aws_access_key_id            = var.aws_access_key_id
  aws_secret_access_key        = var.aws_secret_access_key
  ecr_repository_name           = module.app.ecr_repository_name
  ecs_cluster_name             = module.app.ecs_cluster_name
  ecs_service_name             = module.app.ecs_service_name
  github_personal_access_token = var.github_personal_access_token
  github_repository_name       = var.github_repository_name
  task_definition_family       = module.app.ecs_task_definition_family

  depends_on = [module.app]
}

