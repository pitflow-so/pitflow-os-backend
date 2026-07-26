package br.com.pitflow.operation.infrastructure.outbox;

import br.com.pitflow.common.core.gateway.MessagePublisherGateway;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

class OutboxPublicationServiceTest {

    @Test
    void marksMessageAsPublishedAfterSqsAcknowledges() {
        MessagePublisherGateway publisher = mock(
                MessagePublisherGateway.class
        );
        OutboxStateService stateService = mock(OutboxStateService.class);
        var service = new OutboxPublicationService(
                publisher,
                stateService,
                300
        );
        UUID lockId = UUID.randomUUID();
        var message = claimedMessage(lockId);

        service.publish(message, lockId);

        verify(publisher).send(
                "service-order-orchestrator-queue",
                "{\"type\":\"ServiceOrderBudgetApproved\"}"
        );
        verify(stateService).markPublished(message.getId(), lockId);
    }

    @Test
    void releasesMessageWithBackoffWhenSqsFails() {
        MessagePublisherGateway publisher = mock(
                MessagePublisherGateway.class
        );
        OutboxStateService stateService = mock(OutboxStateService.class);
        var service = new OutboxPublicationService(
                publisher,
                stateService,
                300
        );
        UUID lockId = UUID.randomUUID();
        var message = claimedMessage(lockId);
        doThrow(new IllegalStateException("SQS unavailable"))
                .when(publisher)
                .send(
                        "service-order-orchestrator-queue",
                        "{\"type\":\"ServiceOrderBudgetApproved\"}"
                );

        service.publish(message, lockId);

        verify(stateService).releaseForRetry(
                message.getId(),
                lockId,
                message.getAttempts(),
                300,
                "SQS unavailable"
        );
    }

    private OutboxMessageJpa claimedMessage(UUID lockId) {
        var message = new OutboxMessageJpa(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ServiceOrderBudgetApproved",
                1,
                "service-order-orchestrator-queue",
                "{\"type\":\"ServiceOrderBudgetApproved\"}",
                Instant.parse("2026-07-26T22:00:00Z")
        );
        message.claim(
                lockId,
                Instant.parse("2026-07-26T22:01:00Z")
        );
        return message;
    }
}
