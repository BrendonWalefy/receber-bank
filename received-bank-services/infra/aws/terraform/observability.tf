resource "aws_cloudwatch_log_group" "services" {
  for_each = toset(local.services)

  name              = "/aws/eks/${local.name}/${each.key}"
  retention_in_days = 30

  tags = local.common_tags
}

resource "aws_cloudwatch_log_group" "msk" {
  name              = "/aws/msk/${local.name}"
  retention_in_days = 30

  tags = local.common_tags
}
