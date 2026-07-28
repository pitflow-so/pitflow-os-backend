package br.com.pitflow.operation.infrastructure.outbox;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.event.ServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.event.ServiceOrderReadyForExecution;
import br.com.pitflow.operation.core.event.ServiceOrderCancelled;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class JpaOperationEventGatewayAdapter implements OperationEventGateway {
    private static final int SCHEMA_VERSION = 1;
    private static final String TYPE = "ServiceOrderBudgetApproved";
    private static final String DESTINATION =
            "service-order-orchestrator-queue";
    private static final String SCHEMA_VERSION_FIELD = "schemaVersion";
    private static final String MESSAGE_ID_FIELD = "messageId";
    private static final String OCCURRED_AT_FIELD = "occurredAt";
    private static final String CORRELATION_ID_FIELD = "correlationId";
    private static final String CAUSATION_ID_FIELD = "causationId";
    private static final String SAGA_ID_FIELD = "sagaId";
    private static final String SERVICE_ORDER_ID_FIELD = "serviceOrderId";
    private static final String PAYLOAD_FIELD = "payload";
    private static final String PAYMENT_ID_FIELD = "paymentId";

    private final SpringOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public JpaOperationEventGatewayAdapter(
            SpringOutboxRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveBudgetApproved(ServiceOrderBudgetApproved event) {
        repository.save(new OutboxMessageJpa(
                event.messageId(),
                event.serviceOrderId(),
                TYPE,
                SCHEMA_VERSION,
                DESTINATION,
                serialize(event),
                event.occurredAt()
        ));
    }

    @Override
    public void saveAwaitingPayment(ServiceOrderAwaitingPayment event) {
        repository.save(new OutboxMessageJpa(
                event.messageId(),
                event.serviceOrderId(),
                "ServiceOrderAwaitingPayment",
                SCHEMA_VERSION,
                DESTINATION,
                serialize(event),
                event.occurredAt()
        ));
    }

    @Override
    public void saveReadyForExecution(ServiceOrderReadyForExecution event) {
        repository.save(new OutboxMessageJpa(
                event.messageId(),
                event.serviceOrderId(),
                "ServiceOrderReadyForExecution",
                SCHEMA_VERSION,
                DESTINATION,
                serialize(event),
                event.occurredAt()
        ));
    }

    @Override
    public void saveCancelled(ServiceOrderCancelled event) {
        repository.save(new OutboxMessageJpa(event.messageId(), event.serviceOrderId(),
                "ServiceOrderCancelled", SCHEMA_VERSION, DESTINATION, serialize(event), event.occurredAt()));
    }

    private String serialize(ServiceOrderBudgetApproved event) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put(SCHEMA_VERSION_FIELD, SCHEMA_VERSION);
        envelope.put(MESSAGE_ID_FIELD, event.messageId());
        envelope.put("type", TYPE);
        envelope.put(OCCURRED_AT_FIELD, event.occurredAt().toString());
        envelope.put(CORRELATION_ID_FIELD, event.correlationId());
        envelope.put(CAUSATION_ID_FIELD, null);
        envelope.put(SAGA_ID_FIELD, null);
        envelope.put(SERVICE_ORDER_ID_FIELD, event.serviceOrderId());
        envelope.put(PAYLOAD_FIELD, Map.of(
                "amount", Map.of(
                        "amount", event.amount().toPlainString(),
                        "currency", "BRL"
                )
        ));

        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Could not serialize ServiceOrderBudgetApproved",
                    exception
            );
        }
    }

    private String serialize(ServiceOrderAwaitingPayment event) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put(SCHEMA_VERSION_FIELD, SCHEMA_VERSION);
        envelope.put(MESSAGE_ID_FIELD, event.messageId());
        envelope.put("type", "ServiceOrderAwaitingPayment");
        envelope.put(OCCURRED_AT_FIELD, event.occurredAt().toString());
        envelope.put(CORRELATION_ID_FIELD, event.correlationId());
        envelope.put(CAUSATION_ID_FIELD, event.causationId());
        envelope.put(SAGA_ID_FIELD, event.sagaId());
        envelope.put(SERVICE_ORDER_ID_FIELD, event.serviceOrderId());
        envelope.put(PAYLOAD_FIELD, Map.of(
                PAYMENT_ID_FIELD, event.paymentId()
        ));
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Could not serialize ServiceOrderAwaitingPayment",
                    exception
            );
        }
    }

    private String serialize(ServiceOrderReadyForExecution event) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put(SCHEMA_VERSION_FIELD, SCHEMA_VERSION);
        envelope.put(MESSAGE_ID_FIELD, event.messageId());
        envelope.put("type", "ServiceOrderReadyForExecution");
        envelope.put(OCCURRED_AT_FIELD, event.occurredAt().toString());
        envelope.put(CORRELATION_ID_FIELD, event.correlationId());
        envelope.put(CAUSATION_ID_FIELD, event.causationId());
        envelope.put(SAGA_ID_FIELD, event.sagaId());
        envelope.put(SERVICE_ORDER_ID_FIELD, event.serviceOrderId());
        envelope.put(PAYLOAD_FIELD, Map.of(
                PAYMENT_ID_FIELD, event.paymentId()
        ));
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Could not serialize ServiceOrderReadyForExecution",
                    exception
            );
        }
    }

    private String serialize(ServiceOrderCancelled event) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put(SCHEMA_VERSION_FIELD, SCHEMA_VERSION);
        envelope.put(MESSAGE_ID_FIELD, event.messageId());
        envelope.put("type", "ServiceOrderCancelled");
        envelope.put(OCCURRED_AT_FIELD, event.occurredAt().toString());
        envelope.put(CORRELATION_ID_FIELD, event.correlationId());
        envelope.put(CAUSATION_ID_FIELD, event.causationId());
        envelope.put(SAGA_ID_FIELD, event.sagaId());
        envelope.put(SERVICE_ORDER_ID_FIELD, event.serviceOrderId());
        envelope.put(PAYLOAD_FIELD, Map.of(PAYMENT_ID_FIELD, event.paymentId(), "reason", event.reason()));
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize ServiceOrderCancelled", exception);
        }
    }
}
