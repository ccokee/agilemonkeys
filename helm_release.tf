resource "helm_release" "agilemonkeys" {
  name       = "agilemonkeys"
  repository = "https://github.com/ccokee/agilemonkeys-helm"
  chart      = "agilemonkeys"

  values = [
    "${file("kubernetes/helm/charts/agilemonkeys/values.yaml")}"
  ]
}