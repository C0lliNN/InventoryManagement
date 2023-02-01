variable "aws_access_key_id" {
  type = string
}

variable "aws_secret_access_key" {
  type = string
}

variable "region" {
  type = string
}

variable "db_username" {
  type = string
}

variable "db_password" {
  type = string
}

variable "app_name" {
  type = string
}

variable "mongo_org_id" {
  type = string
}

variable "mongodbatlas_public_key" {
  type = string
}

variable "mongodbatlas_private_key" {
  type = string
}

variable "github_personal_access_token" {
  type = string
}

variable "cpu" {
  type = number
  default = 1024
}

variable "memory" {
  type = number
  default = 2048
}

variable "github_repository_name" {
  type = string
}