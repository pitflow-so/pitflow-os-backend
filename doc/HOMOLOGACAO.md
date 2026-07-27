# Homologação ponta a ponta — PitFlow

Este documento descreve a homologação do fluxo principal do PitFlow após a
separação do monólito em microsserviços.

## Ambiente

- API pública: `https://85ufbygqvi.execute-api.us-east-1.amazonaws.com`
- Operation: `/operation/**`
- Registry: `/registry/**`
- Inventory: `/inventory/**`
- Autenticação do cliente: `/auth/customer`
- Formulário de orçamento: `/customer/budget`
- Swagger Operation: `/operation/swagger-ui/index.html`
- Swagger Registry: `/registry/swagger-ui/index.html`
- Swagger Inventory: `/inventory/swagger-ui/index.html`

Os identificadores e resultados da seção "Evidência da execução" pertencem ao
ambiente de homologação e não devem ser reutilizados como massa fixa.

## Pré-requisitos

- `curl` e `jq`;
- cliente com CPF válido e e-mail acessível;
- veículo vinculado ao cliente;
- mecânico cadastrado;
- serviço e peça disponíveis no Inventory;
- variáveis locais:

```bash
export API_URL="https://85ufbygqvi.execute-api.us-east-1.amazonaws.com"
export CUSTOMER_CPF="78177454048"
export MECHANIC_USERNAME="<usuario>"
export MECHANIC_PASSWORD="<senha>"
```

Nunca registre tokens JWT reais ou senhas reais neste documento.

## 1. Verificar a saúde dos serviços

```bash
curl -fsS "$API_URL/operation/actuator/health"
curl -fsS "$API_URL/registry/actuator/health"
curl -fsS "$API_URL/inventory/actuator/health"
```

Resultado esperado: HTTP 200 e `status=UP`.

## 2. Autenticar o cliente

```bash
CUSTOMER_TOKEN=$(
  curl -fsS -X POST "$API_URL/auth/customer" \
    -H "Content-Type: application/json" \
    -d "{\"cpf\":\"$CUSTOMER_CPF\"}" |
  jq -r '.token'
)
```

Consultar o cliente:

```bash
CUSTOMER=$(
  curl -fsS "$API_URL/registry/customers/document/$CUSTOMER_CPF" \
    -H "Authorization: Bearer $CUSTOMER_TOKEN"
)

CUSTOMER_ID=$(echo "$CUSTOMER" | jq -r '.id')
CUSTOMER_EMAIL=$(echo "$CUSTOMER" | jq -r '.email')
```

## 3. Consultar ou cadastrar o veículo

```bash
curl -fsS "$API_URL/registry/vehicles/customer/$CUSTOMER_ID" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN"
```

Se necessário, cadastrar:

```bash
VEHICLE=$(
  curl -fsS -X POST "$API_URL/registry/vehicles" \
    -H "Authorization: Bearer $CUSTOMER_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"customerId\":\"$CUSTOMER_ID\",
      \"licensePlate\":\"HML1A26\",
      \"brand\":\"Toyota\",
      \"model\":\"Etios\",
      \"year\":2022
    }"
)

VEHICLE_ID=$(echo "$VEHICLE" | jq -r '.id')
```

## 4. Autenticar o mecânico

```bash
MECHANIC_TOKEN=$(
  curl -fsS -X POST "$API_URL/registry/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\":\"$MECHANIC_USERNAME\",
      \"password\":\"$MECHANIC_PASSWORD\"
    }" |
  jq -r '.token'
)
```

O token deve ser mantido somente em memória e não deve ser copiado para a
documentação.

## 5. Consultar o catálogo

```bash
curl -fsS "$API_URL/inventory/services" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"

curl -fsS "$API_URL/inventory/parts" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Selecionar uma peça com estoque e um serviço ativo:

```bash
export SERVICE_ID="<id-do-servico>"
export PART_ID="<id-da-peca>"
```

## 6. Abrir a ordem de serviço

```bash
ORDER=$(
  curl -fsS -X POST "$API_URL/operation/service-orders" \
    -H "Authorization: Bearer $CUSTOMER_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"customerId\":\"$CUSTOMER_ID\",
      \"vehicleId\":\"$VEHICLE_ID\",
      \"description\":\"Homologacao do fluxo de aprovacao de orcamento\"
    }"
)

ORDER_ID=$(echo "$ORDER" | jq -r '.id')
```

Resultado esperado: HTTP 201 e status `RECEIVED`.

## 7. Realizar o diagnóstico

Iniciar:

```bash
curl -fsS -X PATCH \
  "$API_URL/operation/service-orders/$ORDER_ID/start-diagnosis" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Adicionar serviço:

```bash
curl -fsS -X POST \
  "$API_URL/operation/service-orders/$ORDER_ID/items" \
  -H "Authorization: Bearer $MECHANIC_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"catalogId\":\"$SERVICE_ID\",
    \"quantity\":1,
    \"type\":\"SERVICE\"
  }"
```

Adicionar peça:

```bash
curl -fsS -X POST \
  "$API_URL/operation/service-orders/$ORDER_ID/items" \
  -H "Authorization: Bearer $MECHANIC_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"catalogId\":\"$PART_ID\",
    \"quantity\":1,
    \"type\":\"PART\"
  }"
```

Concluir e enviar o e-mail:

```bash
curl -fsS -X PATCH \
  "$API_URL/operation/service-orders/$ORDER_ID/complete-diagnosis" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Resultado esperado:

- HTTP 204;
- OS em `AWAITING_APPROVAL`;
- e-mail enviado ao endereço do cliente;
- formulário exibindo o valor total correto;
- links assinados de aprovação e recusa.

## 8. Decisão do cliente pelo formulário

O e-mail direciona o cliente, por GET, para:

```text
GET /customer/budget?token=<token-assinado>
```

O GET somente renderiza a confirmação. A mudança de estado ocorre no POST:

```text
POST /customer/budget/confirm
```

A Lambda decodifica o formulário, valida o token e envia a decisão ao
Operation. 

Resultado esperado após aprovação:

```bash
curl -fsS "$API_URL/operation/service-orders/$ORDER_ID" \
  -H "Authorization: Bearer $MECHANIC_TOKEN" |
jq '{id,status,totalAmount,items}'
```

Enquanto Payment e Orchestrator ainda não estiverem integrados, o resultado
esperado é `IN_EXECUTION`. Depois da SAGA, este ponto será alterado para
`PAYMENT_PROCESSING`, seguido de `AWAITING_PAYMENT` e
`READY_FOR_EXECUTION`.

## 9. Validar idempotência da decisão

Regra: a primeira decisão válida vence.

1. Enviar novamente a mesma confirmação.
2. Confirmar que a tela apresenta sucesso.
3. Confirmar que a OS continua no mesmo estado.
4. Tentar o link da decisão oposta.
5. Confirmar resposta de conflito/decisão já registrada.
6. Confirmar novamente que a OS não foi alterada.

Comportamento esperado:

- aprovação repetida não executa a transição novamente;
- recusa repetida não executa o cancelamento novamente;
- decisão oposta não sobrescreve a primeira;
- concorrência simultânea forte ainda depende de optimistic locking e da
  implementação persistente de inbox/outbox.

## 10. Concluir a ordem

Listar ordens em execução:

```bash
curl -fsS "$API_URL/operation/service-orders/in-execution" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Finalizar:

```bash
curl -fsS -X PATCH \
  "$API_URL/operation/service-orders/$ORDER_ID/finish" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Entregar:

```bash
curl -fsS -X PATCH \
  "$API_URL/operation/service-orders/$ORDER_ID/deliver" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Consultar métricas:

```bash
curl -fsS "$API_URL/operation/service-orders/metrics/average-execution-time" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"

curl -fsS "$API_URL/operation/service-orders/$ORDER_ID/duration" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

## 11. Demonstrar compensação da SAGA

Este cenário usa um endpoint acadêmico protegido por JWT de mecânico. Não exige
alterar variável nem refazer deploy.

1. Criar uma nova OS e aprovar o orçamento normalmente.
2. Aguardar a OS chegar a `AWAITING_PAYMENT`. Não realizar o pagamento no
   Checkout Pro.
3. Abrir:

```text
https://<API_ID>.execute-api.us-east-1.amazonaws.com/payment/swagger-ui/index.html
```

4. Clicar em `Authorize` e informar `Bearer <MECHANIC_TOKEN>`.
5. Executar:

```text
POST /payment/homologation/service-orders/{serviceOrderId}/reject
```

Alternativa por terminal:

```bash
curl -fsS -X POST \
  "$API_URL/payment/homologation/service-orders/$ORDER_ID/reject" \
  -H "Authorization: Bearer $MECHANIC_TOKEN"
```

Resultado esperado:

```text
Payment REJECTED
  -> outbox PaymentRejected
  -> SAGA COMPENSATING
  -> outbox CancelServiceOrder
  -> Operation CANCELLED
  -> outbox ServiceOrderCancelled
  -> SAGA FAILED
```

Validar a OS:

```bash
curl -fsS "$API_URL/operation/service-orders/$ORDER_ID" \
  -H "Authorization: Bearer $MECHANIC_TOKEN" |
jq '{id,status}'
```

A chamada repetida é idempotente. Se o pagamento já estiver `APPROVED` ou em
outro estado final, o endpoint retorna HTTP 409 e não inicia compensação.
