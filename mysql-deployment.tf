resource "kubernetes_manifest" "mysqldeployment" {
 provider = kubernetes
 
 manifest = {
  "apiVersion" = "apps/v1"
  "kind" = "Deployment"
  "metadata" = {
    "labels" = {
      "app" = "mysql"
    }
    "name" = "mysql-deployment"
  }
  "spec" = {
    "replicas" = 1
    "selector" = {
      "matchLabels" = {
        "app" = "mysql"
      }
    }
    "template" = {
      "metadata" = {
        "labels" = {
          "app" = "mysql"
        }
      }
      "spec" = {
        "containers" = [
          {
            "env" = [
              {
                "name" = "MYSQL_ROOT_PASSWORD"
                "valueFrom" = {
                  "secretKeyRef" = {
                    "key" = "ROOT_PASSWORD"
                    "name" = "mysql-secrets"
                  }
                }
              },
            ]
            "image" = "mysql:5.7"
            "name" = "mysql"
            "ports" = [
              {
                "containerPort" = 3306
              },
            ]
            "volumeMounts" = [
              {
                "mountPath" = "/var/lib/mysql"
                "name" = "mysql-data"
                "subPath" = "mysql"
              },
            ]
          },
        ]
        "volumes" = [
          {
            "emptyDir" = {}
            "name" = "mysql-data"
          },
        ]
      }
    }
  }
}
}