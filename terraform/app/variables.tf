variable "aws_access_key_id" {
  type = string
}

variable "aws_secret_access_key" {

}

variable "region" {
  type = string
}

variable "app_name" {
  type = string
}

variable "mongodb_database_connection_string" {
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
