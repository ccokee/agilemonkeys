resource "kubernetes_deployment" "nginx" {
  provider = kubernetes
  metadata {
    name = "mysql"
    labels = {
      App = "mysql"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "mysql"
      }
    }
    template {
      metadata {
        labels = {
          App = "mysql"
        }
      }
      spec {
        container {
          image = "mysql:5.7"
          name  = "mysql"

          port {
            container_port = 3306
          }

          resources {
            limits = {
              cpu    = "0.5"
              memory = "512Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "50Mi"
            }
          }
        }
        volumes {
            emptyDir = {}
            name = "mysql-data"
        }
      }
    }
  }
}