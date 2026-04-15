resource "null_resource" "wait_for_eks" {
  depends_on = [
    aws_eks_cluster.pitflow_cluster,
    aws_eks_node_group.pitflow_nodes
  ]

  provisioner "local-exec" {
    command = <<EOT
aws eks wait cluster-active --name pitflow-eks --region us-east-1
EOT
  }
}