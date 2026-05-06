resource "aws_s3_bucket" "documents" {
  bucket = "${local.name}-boleto-documents"

  tags = local.common_tags
}

resource "aws_s3_bucket" "backups" {
  bucket = "${local.name}-backups"

  tags = local.common_tags
}

resource "aws_s3_bucket_versioning" "documents" {
  bucket = aws_s3_bucket.documents.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_versioning" "backups" {
  bucket = aws_s3_bucket.backups.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "documents" {
  bucket = aws_s3_bucket.documents.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "backups" {
  bucket = aws_s3_bucket.backups.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_sns_topic" "alerts" {
  name = "${local.name}-alerts"

  tags = local.common_tags
}

resource "aws_sqs_queue" "boleto_generation_dlq" {
  name                      = "${local.name}-boleto-generation-dlq"
  message_retention_seconds = 1209600

  tags = local.common_tags
}

resource "aws_sqs_queue" "boleto_generation" {
  name                       = "${local.name}-boleto-generation"
  visibility_timeout_seconds = 60
  message_retention_seconds  = 345600

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.boleto_generation_dlq.arn
    maxReceiveCount     = 5
  })

  tags = local.common_tags
}

resource "aws_secretsmanager_secret" "app" {
  name = "${local.name}/app"

  tags = local.common_tags
}

resource "aws_secretsmanager_secret_version" "app" {
  secret_id = aws_secretsmanager_secret.app.id

  secret_string = jsonencode({
    SPRING_DATASOURCE_USERNAME  = var.db_username
    SPRING_DATASOURCE_PASSWORD  = var.db_password
    BOLETO_GENERATION_QUEUE_URL = aws_sqs_queue.boleto_generation.url
  })
}
