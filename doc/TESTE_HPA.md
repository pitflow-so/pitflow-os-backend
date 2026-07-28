# Teste do Horizontal Pod Autoscaler do Operation

O HPA do Operation é definido em `infra/k8s/hpa.yaml`. O cluster, Metrics Server
e demais componentes pertencem ao `pitflow-cluster-kubernetes`.

## Pré-requisitos

- AWS CLI autenticada;
- `kubectl` configurado para o EKS;
- Operation implantado no namespace `pitflow`;
- Metrics Server saudável.

```bash
aws eks update-kubeconfig --name pitflow-eks --region us-east-1
kubectl top pods -n pitflow
kubectl get hpa -n pitflow
```

## Acompanhamento

Em terminais separados:

```bash
kubectl get hpa -n pitflow -w
kubectl get pods -n pitflow -l app.kubernetes.io/name=pitflow-backend -w
```

Confirme os labels reais antes do teste:

```bash
kubectl get deployment pitflow-backend -n pitflow --show-labels
```

## Geração de carga

Use uma rota GET do próprio Operation e um JWT válido. Ajuste a concorrência
para o ambiente acadêmico:

```bash
API_URL="https://85ufbygqvi.execute-api.us-east-1.amazonaws.com"
SERVICE_ORDER_ID="<uuid-de-uma-os>"
TOKEN="<jwt>"

seq 1 500 | xargs -n1 -P20 -I{} curl -fsS -o /dev/null \
  -H "Authorization: Bearer $TOKEN" \
  "$API_URL/operation/service-orders/$SERVICE_ORDER_ID"
```

Durante e após a carga, registre:

- réplicas atuais/desejadas no HPA;
- CPU observada;
- criação e remoção dos pods;
- ausência de erros no rollout e nos logs.

O teste valida escalabilidade do deployment, não a lógica funcional da API.
