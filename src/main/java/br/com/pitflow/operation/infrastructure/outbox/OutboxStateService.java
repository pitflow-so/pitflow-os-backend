package br.com.pitflow.operation.infrastructure.outbox;

import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

public class OutboxStateService {
    private final SpringOutboxRepository repository;
    private final Clock clock;

    public OutboxStateService(
            SpringOutboxRepository repository,
            Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public void markPublished(UUID messageId, UUID lockId) {
        var message = find(messageId);
        message.markPublished(lockId, clock.instant());
    }

    @Transactional
    public void releaseForRetry(
            UUID messageId,
            UUID lockId,
            int attempts,
            int maxBackoffSeconds,
            String error
    ) {
        long backoffSeconds = Math.min(
                maxBackoffSeconds,
                1L << Math.min(Math.max(attempts - 1, 0), 20)
        );
        var message = find(messageId);
        message.releaseForRetry(
                lockId,
                clock.instant().plus(Duration.ofSeconds(backoffSeconds)),
                error
        );
    }

    private OutboxMessageJpa find(UUID messageId) {
        return repository.findById(messageId)
                .orElseThrow(() -> new IllegalStateException(
                        "Outbox message not found: " + messageId
                ));
    }
}
