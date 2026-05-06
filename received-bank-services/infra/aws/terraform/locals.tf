locals {
  name = "${var.project_name}-${var.environment}"

  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
  }

  services = [
    "boleto-service",
    "query-service",
    "payment-service",
    "notification-service"
  ]
}
