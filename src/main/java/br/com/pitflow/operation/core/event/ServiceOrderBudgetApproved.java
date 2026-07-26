package br.com.pitflow.operation.core.event;

import br.com.pitflow.operation.core.entity.ServiceOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceOrderBudgetApproved(
        UUID messageId,
        UUID correlationId,
        UUID serviceOrderId,
        BigDecimal amount,
        Instant occurredAt
) {
    public static ServiceOrderBudgetApproved from(ServiceOrder serviceOrder) {
        return new ServiceOrderBudgetApproved(
                UUID.randomUUID(),
                serviceOrder.getId(),
                serviceOrder.getId(),
                serviceOrder.getTotalAmount(),
                Instant.now()
        );
    }
}
