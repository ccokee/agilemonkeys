# The configuration for the `remote` backend.
terraform {
    backend "remote" {
    # The name of your Terraform Cloud organization.
    organization = "unileon"

    # The name of the Terraform Cloud workspace to store Terraform state files in.
    workspaces {
        name = "agilemonkeys"
    }
    }
}

data "aws_availability_zones" "available" {}

locals {
  cluster_name = "tag-eks-${random_string.suffix.result}"
}

resource "random_string" "suffix" {
  length  = 8
  special = false
}