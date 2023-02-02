variable "aws_access_key_id" {
  type = string
  description = "AWS ACCESS KEY ID. It's required to perform authorized actions"
}

variable "aws_secret_access_key" {
  type = string
  description = "AWS SECRET ACCESS KEY. It's required to perform authorized actions"
}

variable "region" {
  type = string
  description = "AWS Region to deploy resources"
}

variable "db_username" {
  type = string
  description = "Username of the database user to be created and used"
}

variable "db_password" {
  type = string
  description = "Password of the database user to be created and used"
}

variable "app_name" {
  type = string
  description = "Name of the application"
}

variable "mongo_org_id" {
  type = string
  description = "Mongo organization id used to create resources."
}

variable "mongodbatlas_public_key" {
  type = string
  description = "Public key used to perform authorized actions"
}

variable "mongodbatlas_private_key" {
  type = string
  description = "Private key used to perform authorized actions"
}

variable "github_personal_access_token" {
  type = string
  description = "GitHub token used to by terraform to perform authorized actions"
}

variable "github_repository_name" {
  type = string
  description = "Name of Github Repository where application code is stored"
}

variable "cpu" {
  type = number
  default = 1024
  description = "The amount of CPU allocated by the ECS task. 1024 = 1 CPU"
}

variable "memory" {
  type = number
  default = 2048
  description = "The amount of RAM allocated by the ECS task. 2048 = 2GB"
}