# The configuration for the `remote` backend.
terraform {
    backend "remote" {
    # The name of your Terraform Cloud organization.
    organization = "The-Agile-Monkeys"

    # The name of the Terraform Cloud workspace to store Terraform state files in.
    workspaces {
        name = "the-agile-monkeys"
    }
    }
}

# An example resource that does nothing.
provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
}

provider "aws" {
  region = var.region
}

data "aws_availability_zones" "available" {}

locals {
  cluster_name = "tag-eks-${random_string.suffix.result}"
}

resource "random_string" "suffix" {
  length  = 8
  special = false
}