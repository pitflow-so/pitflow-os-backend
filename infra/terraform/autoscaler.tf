resource "helm_release" "cluster_autoscaler" {
  provider = helm.eks

  name       = "cluster-autoscaler"
  namespace  = "kube-system"

  repository = "https://kubernetes.github.io/autoscaler"
  chart      = "cluster-autoscaler"
  version = "9.29.0"

  depends_on = [
    null_resource.wait_for_eks,
    aws_eks_node_group.pitflow_nodes
  ]

  set {
    name  = "autoDiscovery.clusterName"
    value = aws_eks_cluster.pitflow_cluster.name
  }

  set {
    name  = "awsRegion"
    value = "us-east-1"
  }

  set {
    name  = "rbac.create"
    value = "true"
  }

  set {
    name  = "extraArgs.balance-similar-node-groups"
    value = "true"
  }

  set {
    name  = "extraArgs.scale-down-unneeded-time"
    value = "5m"
  }

  set {
    name  = "fullnameOverride"
    value = "cluster-autoscaler-pitflow"
  }

  set {
    name  = "extraArgs.v"
    value = "4"
  }

  wait    = true
  timeout = 600
}