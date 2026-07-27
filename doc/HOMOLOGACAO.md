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
Operation. Não utilizar diretamente o endpoint interno/deprecado de decisão.

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

## Evidência da execução de 26/07/2026

| Verificação | Resultado |
|---|---|
| Health Operation, Registry e Inventory | Aprovado — HTTP 200/UP |
| Autenticação do cliente por CPF | Aprovado |
| Cliente | `366941cf-9853-4514-ae99-1e1ea2b984ea` |
| E-mail validado | `rafaelsmoreiras@gmail.com` |
| Veículo | `0b05f25a-21e2-4caf-8dbd-d34f7d9dae38` / `TST1A26` |
| OS | `1c2030d2-1e84-46d8-90d9-f5fc2bf32462` |
| Serviço | Alinhamento e Balanceamento — R$ 150,00 |
| Peça | Pastilha de freio — R$ 100,99 |
| Total | R$ 250,99 |
| E-mail enviado | Aprovado |
| Aprovação pelo formulário | Aprovado |
| Estado após aprovação | `IN_EXECUTION` |
| Repetição da mesma decisão | Aprovado — tela de sucesso e OS permaneceu em `IN_EXECUTION` |
| Decisão oposta | Proteção aprovada — HTTP 409 e OS permaneceu em `IN_EXECUTION` |
| Mensagem da decisão oposta | Aprovado — informa que uma decisão já foi registrada |

## Achados

1. O documento anterior ainda utilizava URLs locais, token JWT fixo expirado,
   endpoint interno de decisão e exemplos do monólito.
2. O usuário `pereira/teste123` do documento anterior não estava disponível no
   ambiente atual; foi utilizado um mecânico exclusivo de homologação.
3. JSON enviado pelo Windows PowerShell sem bytes UTF-8 explícitos, contendo
   caracteres acentuados, resultou em erro de parsing e HTTP 500. O cliente
   deve enviar UTF-8, mas o backend também deve mapear falha de parsing para
   HTTP 400.
4. Os textos do catálogo apareceram com indícios de mojibake em algumas
   respostas (`ServiÃ§o`, por exemplo). Validar codificação dos dados/migrations
   e cabeçalhos HTTP.
5. Não registrar JWTs nem URLs contendo tokens nos logs ou neste documento.
6. A decisão oposta foi corretamente recusada pelo Operation com HTTP 409. A
   Lambda inicialmente apresentava erro genérico, mas foi ajustada e homologada
   para informar que uma decisão já foi registrada, preservando HTTP 500 apenas
   para falhas técnicas.
7. Melhoria visual não bloqueante: substituir o título genérico "Algo deu
   errado" por "Decisão já registrada" na página de conflito.

## Evidência da SAGA até AWAITING_PAYMENT — 27/07/2026

| Verificação | Resultado |
|---|---|
| OS | `11136e83-72c7-4681-8c41-3e7044fcacc9` |
| Total | R$ 250,99 |
| Aprovação pelo formulário | Aprovada |
| Evento inicial | `ServiceOrderBudgetApproved` publicado pela outbox |
| Incidente encontrado | `occurredAt` numérico em notação científica incompatível com consumidor ISO-8601 |
| Correção do produtor | Operation publica novos timestamps como string ISO-8601 |
| Compatibilidade do consumidor | Orchestrator aceita ISO-8601 e epoch legado |
| Recuperação | Uma mensagem redirecionada da DLQ para a fila principal |
| SAGA | `6601b319-4904-4e64-861f-8fae9718dd78` |
| Payment | `27ce6ae9-fe83-42ae-921b-371888e58242` |
| Estado final da OS | `AWAITING_PAYMENT` |
| Estado final da SAGA | `AWAITING_PAYMENT` |
| Inbox do Orchestrator | Três mensagens correlacionadas persistidas |
| Filas e DLQs ao final | Todas vazias |
| E-mail com Checkout Pro | Enviado para `rafaelsmoreiras@gmail.com` |

O redrive reutilizou a mensagem real que havia falhado e comprovou a
retrocompatibilidade. Não foi criada uma segunda aprovação nem uma segunda
SAGA. O próximo teste manual é abrir o e-mail de pagamento e concluir uma
compra com a conta compradora de teste em janela anônima.
