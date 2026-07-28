package br.com.pitflow.operation.infrastructure.outbox;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.event.ServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.event.ServiceOrderReadyForExecution;
import br.com.pitflow.operation.core.event.ServiceOrderCancelled;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class JpaOperationEventGatewayAdapter implements OperationEventGateway {
    private static final int SCHEMA_VERSION = 1;
    private static final String TYPE = "ServiceOrderBudgetApproved";
    private static final String DESTINATION =
            "service-order-orchestrator-queue";
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
        return serialize(event.messageId(), TYPE, event.occurredAt(), event.correlationId(),
                null, null, event.serviceOrderId(), Map.of(
                "amount", Map.of(
                        "amount", event.amount().toPlainString(),
                        "currency", "BRL"
                )
        ));
    }

    private String serialize(ServiceOrderAwaitingPayment event) {
        return serialize(event.messageId(), "ServiceOrderAwaitingPayment", event.occurredAt(),
                event.correlationId(), event.causationId(), event.sagaId(), event.serviceOrderId(),
                Map.of(PAYMENT_ID_FIELD, event.paymentId()));
    }

    private String serialize(ServiceOrderReadyForExecution event) {
        return serialize(event.messageId(), "ServiceOrderReadyForExecution", event.occurredAt(),
                event.correlationId(), event.causationId(), event.sagaId(), event.serviceOrderId(),
                Map.of(PAYMENT_ID_FIELD, event.paymentId()));
    }

    private String serialize(ServiceOrderCancelled event) {
        return serialize(event.messageId(), "ServiceOrderCancelled", event.occurredAt(),
                event.correlationId(), event.causationId(), event.sagaId(), event.serviceOrderId(),
                Map.of(PAYMENT_ID_FIELD, event.paymentId(), "reason", event.reason()));
    }

    @SuppressWarnings("java:S107") // Mirrors the versioned integration-event envelope contract.
    private String serialize(UUID messageId, String type, Instant occurredAt, UUID correlationId,
                             UUID causationId, UUID sagaId, UUID serviceOrderId, Map<String, ?> payload) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("schemaVersion", SCHEMA_VERSION);
        envelope.put("messageId", messageId);
        envelope.put("type", type);
        envelope.put("occurredAt", occurredAt.toString());
        envelope.put("correlationId", correlationId);
        envelope.put("causationId", causationId);
        envelope.put("sagaId", sagaId);
        envelope.put("serviceOrderId", serviceOrderId);
        envelope.put("payload", payload);
        try {
            return objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize " + type, exception);
        }
    }
}
