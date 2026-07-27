package br.com.pitflow.operation.infrastructure.outbox;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.event.ServiceOrderAwaitingPayment;
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

    private String serialize(ServiceOrderBudgetApproved event) {
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("schemaVersion", SCHEMA_VERSION);
        envelope.put("messageId", event.messageId());
        envelope.put("type", TYPE);
        envelope.put("occurredAt", event.occurredAt());
        envelope.put("correlationId", event.correlationId());
        envelope.put("causationId", null);
        envelope.put("sagaId", null);
        envelope.put("serviceOrderId", event.serviceOrderId());
        envelope.put("payload", Map.of(
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
        envelope.put("schemaVersion", SCHEMA_VERSION);
        envelope.put("messageId", event.messageId());
        envelope.put("type", "ServiceOrderAwaitingPayment");
        envelope.put("occurredAt", event.occurredAt());
        envelope.put("correlationId", event.correlationId());
        envelope.put("causationId", event.causationId());
        envelope.put("sagaId", event.sagaId());
        envelope.put("serviceOrderId", event.serviceOrderId());
        envelope.put("payload", Map.of(
                "paymentId", event.paymentId()
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
}
