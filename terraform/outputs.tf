output "ecr_repo_url" {
  value = aws_ecr_repository.app_repository.repository_url
}

output "database_cluster_uri" {
  value = mongodbatlas_advanced_cluster.database.connection_strings.0.standard_srv
}

output "alb_dns" {
  value = aws_alb.app_alb.dns_name
}
