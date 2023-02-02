output "ecr_repository_url" {
  value = aws_ecr_repository.app_repository.repository_url
}

output "ecr_repository_name" {
  value = aws_ecr_repository.app_repository.name
}

output "alb_app_dns" {
  value = aws_alb.alb.dns_name
}

output "ecs_cluster_name" {
  value = module.ecs_cluster.cluster_name
}

output "ecs_service_name" {
  value = aws_ecs_service.inventory_management_service.name
}

output "ecs_task_definition_family" {
  value = aws_ecs_task_definition.task_definition.family
}
