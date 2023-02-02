variable "region" {
  type = string
  description = "AWS Region to deploy resources"
}

variable "app_name" {
  type = string
  description = "Application Name"
}

variable "mongodb_connection_string" {
  type = string
  description = "Connection string used by the application to connect to mongodb"
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
