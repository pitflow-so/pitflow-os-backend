package br.com.pitflow.operation.core.usecase.inputPort;

import java.time.Instant;
import java.util.UUID;

public interface MarkServiceOrderAwaitingPayment {
    Result execute(Command command);

    record Command(
            UUID messageId,
            UUID correlationId,
            UUID sagaId,
            UUID serviceOrderId,
            UUID paymentId,
            String preferenceId,
            String checkoutUrl,
            Instant expiresAt,
            Instant occurredAt
    ) {
    }

    enum Result {
        UPDATED,
        ALREADY_PROCESSED
    }
}
