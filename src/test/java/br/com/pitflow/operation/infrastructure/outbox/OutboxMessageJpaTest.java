package br.com.pitflow.operation.infrastructure.outbox;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OutboxMessageJpaTest {
    @Test
    void supportsClaimPublishRetryAndLeaseValidation() {
        var now = Instant.parse("2026-07-27T20:00:00Z");
        var message = new OutboxMessageJpa(
                UUID.randomUUID(), UUID.randomUUID(), "Event", 1, "queue", "{}", now);
        assertEquals("PENDING", message.getStatus());
        assertEquals(0, message.getAttempts());
        assertEquals("queue", message.getDestination());
        assertNotNull(message.getId());
        assertNotNull(message.getAggregateId());
        assertEquals("Event", message.getMessageType());
        assertEquals("{}", message.getPayload());

        var lock = UUID.randomUUID();
        message.claim(lock, now.plusSeconds(30));
        assertEquals("PROCESSING", message.getStatus());
        assertEquals(1, message.getAttempts());
        assertEquals(lock, message.getLockId());
        assertNotNull(message.getLockedUntil());
        assertThrows(IllegalStateException.class,
                () -> message.markPublished(UUID.randomUUID(), now));

        message.releaseForRetry(lock, now.plusSeconds(60), "temporary");
        assertEquals("PENDING", message.getStatus());
        assertNull(message.getLockId());

        var secondLock = UUID.randomUUID();
        message.claim(secondLock, now.plusSeconds(90));
        message.markPublished(secondLock, now.plusSeconds(1));
        assertEquals("PUBLISHED", message.getStatus());
        assertNull(message.getLockedUntil());
    }
}
