resource "aws_secretsmanager_secret" "database_uri" {
  name = "${var.app_name}-database-uri"
}

resource "aws_secretsmanager_secret_version" "database_uri_value" {
  secret_id = aws_secretsmanager_secret.database_uri.id
  secret_string = "mongodb+srv://${var.db_username}:${var.db_password}@${split("//", mongodbatlas_advanced_cluster.database.connection_strings.0.standard_srv)[1]}/${var.app_name}"
}