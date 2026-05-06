resource "aws_api_gateway_rest_api" "recebimentos" {
  name        = "${local.name}-api"
  description = "API publica de recebimentos com entrada assincrona via SQS."

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = local.common_tags
}

resource "aws_api_gateway_resource" "boletos" {
  rest_api_id = aws_api_gateway_rest_api.recebimentos.id
  parent_id   = aws_api_gateway_rest_api.recebimentos.root_resource_id
  path_part   = "boletos"
}

resource "aws_api_gateway_method" "post_boletos" {
  rest_api_id      = aws_api_gateway_rest_api.recebimentos.id
  resource_id      = aws_api_gateway_resource.boletos.id
  http_method      = "POST"
  authorization    = "NONE"
  api_key_required = false
}

resource "aws_api_gateway_integration" "post_boletos_sqs" {
  rest_api_id             = aws_api_gateway_rest_api.recebimentos.id
  resource_id             = aws_api_gateway_resource.boletos.id
  http_method             = aws_api_gateway_method.post_boletos.http_method
  type                    = "AWS"
  integration_http_method = "POST"
  credentials             = aws_iam_role.api_gateway_sqs.arn
  uri                     = "arn:aws:apigateway:${var.aws_region}:sqs:path/${data.aws_caller_identity.current.account_id}/${aws_sqs_queue.boleto_generation.name}"

  request_parameters = {
    "integration.request.header.Content-Type" = "'application/x-www-form-urlencoded'"
  }

  request_templates = {
    "application/json" = "Action=SendMessage&MessageBody=$util.urlEncode($input.body)"
  }

  depends_on = [aws_iam_role_policy_attachment.api_gateway_sqs]
}

resource "aws_api_gateway_method_response" "post_boletos_accepted" {
  rest_api_id = aws_api_gateway_rest_api.recebimentos.id
  resource_id = aws_api_gateway_resource.boletos.id
  http_method = aws_api_gateway_method.post_boletos.http_method
  status_code = "202"

  response_models = {
    "application/json" = "Empty"
  }
}

resource "aws_api_gateway_integration_response" "post_boletos_accepted" {
  rest_api_id = aws_api_gateway_rest_api.recebimentos.id
  resource_id = aws_api_gateway_resource.boletos.id
  http_method = aws_api_gateway_method.post_boletos.http_method
  status_code = aws_api_gateway_method_response.post_boletos_accepted.status_code

  response_templates = {
    "application/json" = "{\"status\":\"ACCEPTED\"}"
  }

  depends_on = [aws_api_gateway_integration.post_boletos_sqs]
}

resource "aws_api_gateway_deployment" "recebimentos" {
  rest_api_id = aws_api_gateway_rest_api.recebimentos.id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_api_gateway_resource.boletos.id,
      aws_api_gateway_method.post_boletos.id,
      aws_api_gateway_integration.post_boletos_sqs.id,
      aws_api_gateway_integration_response.post_boletos_accepted.id
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "main" {
  rest_api_id   = aws_api_gateway_rest_api.recebimentos.id
  deployment_id = aws_api_gateway_deployment.recebimentos.id
  stage_name    = var.environment

  tags = local.common_tags
}
