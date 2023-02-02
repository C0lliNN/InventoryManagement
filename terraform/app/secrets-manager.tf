resource "aws_secretsmanager_secret" "database_uri" {
  name = "${var.app_name}-mongodb-uri"
}

resource "aws_secretsmanager_secret_version" "database_uri_value" {
  secret_id = aws_secretsmanager_secret.database_uri.id
  secret_string = var.mongodb_database_connection_string
}