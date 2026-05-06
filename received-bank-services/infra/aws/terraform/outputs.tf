output "eks_cluster_name" {
  value = aws_eks_cluster.main.name
}

output "ecr_repositories" {
  value = {
    for name, repo in aws_ecr_repository.services : name => repo.repository_url
  }
}

output "rds_endpoint" {
  value = aws_db_instance.postgres.address
}

output "redis_endpoint" {
  value = aws_elasticache_replication_group.redis.primary_endpoint_address
}

output "msk_bootstrap_brokers" {
  value = aws_msk_cluster.main.bootstrap_brokers
}

output "documents_bucket" {
  value = aws_s3_bucket.documents.bucket
}

output "alerts_topic_arn" {
  value = aws_sns_topic.alerts.arn
}

output "boleto_generation_queue_url" {
  value = aws_sqs_queue.boleto_generation.url
}

output "api_gateway_invoke_url" {
  value = "${aws_api_gateway_stage.main.invoke_url}/boletos"
}
