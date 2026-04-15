# data sources:
data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }

  # Note: Houve um erro então estou excluindo us-east-1e
  filter {
    name   = "availabilityZone"
    values = ["us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d", "us-east-1f"]
  }
}


data "aws_eks_cluster" "cluster" {
  name = aws_eks_cluster.pitflow_cluster.name
}

data "aws_eks_cluster_auth" "cluster" {
  name = aws_eks_cluster.pitflow_cluster.name
}
# Resources:

resource "aws_eks_cluster" "pitflow_cluster" {
  name     = "pitflow-eks"
  role_arn = data.aws_iam_role.lab_role.arn

  vpc_config {
    subnet_ids = data.aws_subnets.default.ids
  }
}

resource "aws_eks_node_group" "pitflow_nodes" {
  cluster_name    = aws_eks_cluster.pitflow_cluster.name
  node_group_name = "pitflow-node-group"
  node_role_arn   = data.aws_iam_role.lab_role.arn

  subnet_ids = data.aws_subnets.default.ids

  scaling_config {
    desired_size = 1
    max_size     = 3
    min_size     = 1
  }

  tags = {
    "k8s.io/cluster-autoscaler/enabled" = "true"
    "k8s.io/cluster-autoscaler/pitflow-eks" = "owned"
  }

  instance_types = ["t3.medium"]
  capacity_type  = "SPOT"
}

output "eks_cluster_name" {
  value = aws_eks_cluster.pitflow_cluster.name
}