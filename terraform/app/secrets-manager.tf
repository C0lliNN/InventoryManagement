resource "aws_secretsmanager_secret" "mongodb_connection_string" {
  name = "${var.app_name}-mongodb-connection-string"
}

resource "aws_secretsmanager_secret_version" "mongodb_connection_string_value" {
  secret_id = aws_secretsmanager_secret.mongodb_connection_string.id
  secret_string = var.mongodb_database_connection_string
}