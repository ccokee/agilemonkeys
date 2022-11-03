resource "kubernetes_service" "mysqlservice" {
 provider = kubernetes
 
  metadata {
    name = "mysql-service"
  }
  spec {
    selector = {
      App = kubernetes_deployment.mysql.spec.0.template.0.metadata[0].labels.App
    }
    port {
      port        = 3306
      target_port = 3306
    }

    type = "LoadBalancer"
  }
}