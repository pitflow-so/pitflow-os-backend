### Para o teste do Horizontal Pod Autoscale (HPA)
Necessário ter `kubectl` e `aws cli`

#### 0. Garantir que o kubectl esteja devidamente configurado
`pitflow-eks` é o nome do cluster.
```bash
aws eks update-kubeconfig --name pitflow-eks --region us-east-1
```
#### 01. Monitorar o consumo de CPU
```bash
  kubectl get hpa pitflow-hpa -w
```
#### 02. Monitarar a criação de novos Pods e shutdown quando reduzir o consumo de recursos:
```bash
  kubectl get pods -l app=pitflow-backend -w
```

#### 03. Obter o URL do loadbalance:
```bash
 kubectl get svc
```

#### 04. Executar script para realizar requests:
OBS: a placa passado como query parameter `"ODA1234"` existe cadastrada pela migrations, por isso foi passada no teste
```bash
for i in {1..30}; do while true; do curl -s -o /dev/null -X GET "http://<URL_DO_LOAD_BALANCER>/registry/vehicles/plate/ODA1234" -H "accept: */*"; done & done

```
#### 05. Observar os pods subindo
