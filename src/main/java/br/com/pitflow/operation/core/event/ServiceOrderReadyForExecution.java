package br.com.pitflow.operation.core.event;

import java.time.Instant;
import java.util.UUID;

public record ServiceOrderReadyForExecution(
        UUID messageId,
        UUID correlationId,
        UUID causationId,
        UUID sagaId,
        UUID serviceOrderId,
        UUID paymentId,
        Instant occurredAt
) {
}
