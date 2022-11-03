resource "kubernetes_manifest" "mysqlsecret" {
 provider = kubernetes
 
 manifest = {
    "apiVersion" = "v1"
    "data" = {
        "ROOT_PASSWORD" = "YWdpbGVtb25rZXlz"
    }
    "kind" = "Secret"
    "metadata" = {
        "name" = "mysql-secrets"
    }
    "type" = "Opaque"
    }
}