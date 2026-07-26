package br.com.pitflow.operation.infrastructure.outbox;

import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.UUID;

public class OutboxPublisherScheduler {
    private final OutboxClaimService claimService;
    private final OutboxPublicationService publicationService;
    private final int batchSize;
    private final Duration lease;

    public OutboxPublisherScheduler(
            OutboxClaimService claimService,
            OutboxPublicationService publicationService,
            int batchSize,
            Duration lease
    ) {
        this.claimService = claimService;
        this.publicationService = publicationService;
        this.batchSize = batchSize;
        this.lease = lease;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    public void publishPendingMessages() {
        UUID lockId = UUID.randomUUID();
        var messages = claimService.claim(lockId, batchSize, lease);
        messages.forEach(message ->
                publicationService.publish(message, lockId)
        );
    }
}
