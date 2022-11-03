resource "kubernetes_manifest" "mysqlservice" {
 provider = kubernetes
 
 manifest = {
  "apiVersion" = "v1"
  "kind" = "Service"
  "metadata" = {
    "name" = "mysql-service"
  }
  "spec" = {
    "ports" = [
      {
        "port" = 3306
        "protocol" = "TCP"
        "targetPort" = 3306
      },
    ]
    "selector" = {
      "app" = "mysql"
    }
  }
}
}