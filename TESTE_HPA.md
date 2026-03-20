Para o teste do Horizontal Pod Autoscale (HPA)

- 01 Monitorar o consumo de CPU
  kubectl get hpa pitflow-hpa -w


- 02 Monitarar a criação de novos Pods e shutdown quando reduzir o consumo de recursos:
  kubectl get pods -l app=pitflow-backend -w


- 03 Obter o URL do loadbalance:
 kubectl get svc

- Executar script para realizar requests:
for i in {1..30}; do while true; do curl -s -o /dev/null -X GET "http://<URL_DO_LOAD_BALANCER>/registry/vehicles/plate/ODA1234" -H "accept: */*"; done & done

- Observar os pods subindo