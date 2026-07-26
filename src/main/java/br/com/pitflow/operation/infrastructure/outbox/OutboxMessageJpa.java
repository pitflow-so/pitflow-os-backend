package br.com.pitflow.operation.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

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
}
