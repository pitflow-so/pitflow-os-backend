## Api
👉 Documentação da API disponível em: http://localhost:8080/swagger-ui/index.html

### 📋Caso de uso
Cliente relata problema no carro, então vamos realizar o cadastro do cliente e solilcitar um reparo.

### 01 - Cadastro de cliente
Obs: Precisa ser um CPF válido, utilizar algum validador online.
```curl
curl -X 'POST' \
  'http://localhost:8080/registry/customers' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Pedro Santos",
  "document": "42184902012",
  "phone": "2799550255"
}'
```
Response:
```json
{
  "id": "7079af59-5921-4e60-96b3-63f7ed49404f",
  "name": "Pedro Santos",
  "document": "42184902012",
  "phone": "2799550255"
}
```
### 2 - Cadastro de veículo
```curl
curl -X 'POST' \
  'http://localhost:8080/registry/vehicles' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
  "licensePlate": "OCC1234",
  "brand": "Toyota",
  "model": "Etios",
  "year": 2022
}'
```
response:
```json
{
  "id": "82115798-899b-437b-aca9-96d2c1f97eed",
  "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
  "licensePlate": "OCC1234",
  "brand": "Toyota",
  "model": "Etios",
  "year": 2022
}
```

### 3 - Solicitar reparo
```curl
curl -X 'POST' \
  'http://localhost:8080/operation/service-orders' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
  "vehicleId": "82115798-899b-437b-aca9-96d2c1f97eed",
  "description": "Quero solicitar o balanceamento do carro"
}'
````

response:
```json
{
  "id": "afdeced8-58d0-4f6a-8355-614e160aa834",
  "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
  "vehicleId": "82115798-899b-437b-aca9-96d2c1f97eed",
  "description": "Quero solicitar o balanceamento do carro",
  "status": "RECEIVED",
  "totalAmount": 0,
  "createdAt": "2026-01-20T23:05:04.943459472",
  "finishedAt": null,
  "items": [],
  "cancellationDescription": null
}
```
### 04 - Cadastro do mecânico
Nesse ponto precisamos cadastrar um mecânico pois ele é quem irá realizar o reparo e tem permissão para adicionar os items na OS.
Obs: o password foi: 
```curl
curl -X 'POST' \
  'http://localhost:8080/registry/mechanics' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "name": "Edmundo pereira",
  "username": "pereira",
  "password": "teste123"
}'
```
response:
```json
{
  "id": "6f62bfa6-1843-4849-b147-a0e05996d9b4",
  "name": "Edmundo pereira",
  "username": "pereira"
}
```
### 05 - Autenticação do mecânico
```curl
curl -X 'POST' \
  'http://localhost:8080/registry/auth/login' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "pereira",
  "password": "teste123"
}'
```
response:
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf",
  "mechanic": {
    "id": "6f62bfa6-1843-4849-b147-a0e05996d9b4",
    "name": "Edmundo pereira",
    "username": "pereira"
  }
}
```

### 06 - Lista operações pendentes

Aqui o mecânico verifica as operações que foram solicitadas em ordem da mais antiga para mais recente.
Obs: Precisa estar autenticado.
```curl
curl -X 'GET' \
  'http://localhost:8080/operation/service-orders' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```
response:
```json
[
  {
    "id": "afdeced8-58d0-4f6a-8355-614e160aa834",
    "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
    "vehicleId": "82115798-899b-437b-aca9-96d2c1f97eed",
    "description": "Quero solicitar o balanceamento do carro",
    "status": "RECEIVED",
    "totalAmount": 0,
    "createdAt": "2026-01-20T23:05:04.943459",
    "finishedAt": null,
    "items": [],
    "cancellationDescription": null
  }
]
```
### 07 - Inicia análise técnica, para definir serviços e peças
```curl
curl -X 'PATCH' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/start-diagnosis' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```

### 08 - Adiciona item de serviço
Aqui o mecânico adiciona os serviços e peças, caso seja necessário.
Estou utilizando serviços e peças já cadastrados via script de inicialização do banco, mas poderia cadastrar peças e serviços também.

Serviço (`3ad26f19-d339-446c-8185-e8bf4235ac1e`):
```curl
curl -X 'POST' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/items' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf' \
  -H 'Content-Type: application/json' \
  -d '{
  "serviceOrderId": "afdeced8-58d0-4f6a-8355-614e160aa834",
  "catalogId": "3ad26f19-d339-446c-8185-e8bf4235ac1e",
  "quantity": 1,
  "type": "SERVICE"
}'
```
Peça (`ac0580ab-d45f-44c7-b07d-83a33b8c709b`):
```curl
curl -X 'POST' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/items' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf' \
  -H 'Content-Type: application/json' \
  -d '{
  "serviceOrderId": "afdeced8-58d0-4f6a-8355-614e160aa834",
  "catalogId": "ac0580ab-d45f-44c7-b07d-83a33b8c709b",
  "quantity": 1,
  "type": "PART"
}'
```

### 09 - Finaliza a analise tecnica e notifica o cliente do orçamento
```curl
curl -X 'PATCH' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/complete-diagnosis' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```
### 10 - Cliente consulta a OS
```curl
curl -X 'GET' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834' \
  -H 'accept: */*'
```
response:
```json
{
  "id": "afdeced8-58d0-4f6a-8355-614e160aa834",
  "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
  "vehicleId": "82115798-899b-437b-aca9-96d2c1f97eed",
  "description": "Quero solicitar o balanceamento do carro",
  "status": "AWAITING_APPROVAL",
  "totalAmount": 250.99,
  "createdAt": "2026-01-20T23:05:04.943459",
  "finishedAt": null,
  "items": [
    {
      "catalogId": "3ad26f19-d339-446c-8185-e8bf4235ac1e",
      "description": "Alinhamento e Balanceamento",
      "unitPrice": 150,
      "quantity": 1,
      "totalPrice": 150
    },
    {
      "catalogId": "ac0580ab-d45f-44c7-b07d-83a33b8c709b",
      "description": "Pastilha de freio",
      "unitPrice": 100.99,
      "quantity": 1,
      "totalPrice": 100.99
    }
  ],
  "cancellationDescription": null
}
```
### 11 - Cliente aprova a OS (Decisão do Orçamento)
```curl
curl -X 'PATCH' \
  'http://localhost:8080/operation/service-orders/v2/afdeced8-58d0-4f6a-8355-614e160aa834/budget-decision' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
      "approved": true,
      "reason": ""
  }'
```

### 12 - Mecânico lista as ordens aprovadas para iniciar o serviço
A listagem ocorre da mais antiga para a mais recente.
```curl
curl -X 'GET' \
  'http://localhost:8080/operation/service-orders/in-execution' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```
response:
```json
[
  {
    "id": "afdeced8-58d0-4f6a-8355-614e160aa834",
    "customerId": "7079af59-5921-4e60-96b3-63f7ed49404f",
    "vehicleId": "82115798-899b-437b-aca9-96d2c1f97eed",
    "description": "Quero solicitar o balanceamento do carro",
    "status": "IN_EXECUTION",
    "totalAmount": 250.99,
    "createdAt": "2026-01-20T23:05:04.943459",
    "finishedAt": null,
    "items": [
      {
        "catalogId": "3ad26f19-d339-446c-8185-e8bf4235ac1e",
        "description": "Alinhamento e Balanceamento",
        "unitPrice": 150,
        "quantity": 1,
        "totalPrice": 150
      },
      {
        "catalogId": "ac0580ab-d45f-44c7-b07d-83a33b8c709b",
        "description": "Pastilha de freio",
        "unitPrice": 100.99,
        "quantity": 1,
        "totalPrice": 100.99
      }
    ],
    "cancellationDescription": null
  }
]
```

### 13 - Mêcanico finaliza a OS
```curl
curl -X 'PATCH' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/finish' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```

### 14 - Mêcanico entrega o veículo
```curl
curl -X 'PATCH' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/deliver' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```

### 15 - O mecânico consegue ver o tempo médio das OSs
```curl
curl -X 'GET' \
  'http://localhost:8080/operation/service-orders/metrics/average-execution-time' \
  -H 'accept: */*' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJlaXJhIiwibmFtZSI6IkVkbXVuZG8gcGVyZWlyYSIsInJvbGUiOiJST0xFX01FQ0hBTklDIiwiaWF0IjoxNzY4OTUwNjA1LCJleHAiOjE3Njg5NzIyMDV9.DJaabxS-9xW8vyR_6U7zv_KE4uRIrgU5spTnm_8Bq07FGmuWg7LkKCRPB2J5mrPf'
```
response:
```json
{
  "averageTimeInMinutes": 2.9439103833333333,
  "formattedTime": "2min"
}
```

### 16 - Consultar duração de uma OS
```curl
curl -X 'GET' \
  'http://localhost:8080/operation/service-orders/afdeced8-58d0-4f6a-8355-614e160aa834/duration' \
  -H 'accept: */*'
```
response:
```json
{
  "serviceOrderId": "afdeced8-58d0-4f6a-8355-614e160aa834",
  "durationInMinutes": 2,
  "formattedDuration": "2min",
  "isStillRunning": false
}
```
