provider "aws" {
  region = "eu-west-1"
  assume_role {
    role_arn     = "arn:aws:iam::078766879797:role/admin"
    session_name = "example"
  }
}