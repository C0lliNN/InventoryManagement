output "mongodb_cluster_uri" {
  value = mongodbatlas_advanced_cluster.cluster.connection_strings.0.standard_srv
}
