variable "aws_region" {
  type        = string
  description = "Regiao AWS onde os recursos serao criados."
  default     = "us-east-1"
}

variable "project_name" {
  type        = string
  description = "Nome base usado nos recursos."
  default     = "received-bank"
}

variable "environment" {
  type        = string
  description = "Ambiente da implantacao."
  default     = "dev"
}

variable "vpc_cidr" {
  type        = string
  description = "CIDR principal da VPC."
  default     = "10.0.0.0/16"
}

variable "db_username" {
  type        = string
  description = "Usuario master do RDS."
  default     = "recebimentos"
}

variable "db_password" {
  type        = string
  description = "Senha master do RDS. Use tfvars/secret backend em ambientes reais."
  sensitive   = true
}

variable "eks_node_instance_types" {
  type        = list(string)
  description = "Tipos de instancia para o node group EKS."
  default     = ["t3.medium"]
}

variable "eks_desired_size" {
  type    = number
  default = 2
}

variable "eks_min_size" {
  type    = number
  default = 1
}

variable "eks_max_size" {
  type    = number
  default = 4
}
