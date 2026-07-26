package br.com.pitflow.operation.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "operation_outbox")
public class OutboxMessageJpa {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "message_type", nullable = false, length = 100)
    private String messageType;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "destination", nullable = false, length = 255)
    private String destination;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "lock_id")
    private UUID lockId;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    protected OutboxMessageJpa() {
    }

    public OutboxMessageJpa(
            UUID id,
            UUID aggregateId,
            String messageType,
            int schemaVersion,
            String destination,
            String payload,
            Instant occurredAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.messageType = messageType;
        this.schemaVersion = schemaVersion;
        this.destination = destination;
        this.payload = payload;
        this.status = "PENDING";
        this.attempts = 0;
        this.availableAt = occurredAt;
        this.createdAt = occurredAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public String getDestination() {
        return destination;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public UUID getLockId() {
        return lockId;
    }

    public void claim(UUID newLockId, Instant leaseUntil) {
        status = "PROCESSING";
        attempts++;
        lockId = newLockId;
        lockedUntil = leaseUntil;
        lastError = null;
    }

    public void markPublished(UUID expectedLockId, Instant now) {
        validateLock(expectedLockId);
        status = "PUBLISHED";
        publishedAt = now;
        lockId = null;
        lockedUntil = null;
        lastError = null;
    }

    public void releaseForRetry(
            UUID expectedLockId,
            Instant nextAttempt,
            String error
    ) {
        validateLock(expectedLockId);
        status = "PENDING";
        availableAt = nextAttempt;
        lockId = null;
        lockedUntil = null;
        lastError = truncate(error);
    }

    private void validateLock(UUID expectedLockId) {
        if (!"PROCESSING".equals(status)
                || !expectedLockId.equals(lockId)) {
            throw new IllegalStateException(
                    "Outbox message is not owned by the expected lease"
            );
        }
    }

    private String truncate(String error) {
        if (error == null) {
            return null;
        }
        return error.substring(0, Math.min(error.length(), 1000));
    }
}
