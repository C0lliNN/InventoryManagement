variable "project_name" {
  type = string
  description = "The name of project to be created"
}

variable "cluster_name" {
  type = string
  description = "The name of cluster to be created"
}

variable "database_name" {
  type = string
  description = "The name of cluster to be created"
}

variable "mongo_org_id" {
  type = string
  description = "ID of the mongo organization. It's necessary to associate the project"
}

variable "database_username" {
  type = string
  description = "The username used by the application to connect to mongo"
}

variable "database_password" {
  type = string
  description = "The password used by the application to connect to mongo"
}

variable "region" {
  type = string
  description = "Region to deploy the Mongo Cluster"
}