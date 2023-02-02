output "mongodb_cluster_uri" {
  value = module.mongodb_cluster.mongodb_cluster_uri
}

output "ecr_repository_url" {
  value = module.app.ecr_repository_url
}

output "alb_app_dns" {
  value = module.app.alb_app_dns
}
