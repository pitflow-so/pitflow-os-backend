package br.com.pitflow.operation.infrastructure.outbox;

import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class OutboxClaimService {
    private final SpringOutboxRepository repository;
    private final Clock clock;

    public OutboxClaimService(
            SpringOutboxRepository repository,
            Clock clock
    ) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public List<OutboxMessageJpa> claim(
            UUID lockId,
            int batchSize,
            Duration lease
    ) {
        var leaseUntil = clock.instant().plus(lease);
        var messages = repository.findClaimableForUpdate(batchSize);
        messages.forEach(message -> message.claim(lockId, leaseUntil));
        repository.flush();
        return List.copyOf(messages);
    }
}
