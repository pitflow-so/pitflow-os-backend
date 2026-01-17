package br.com.pitflow.operation.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class ServiceOrder {
    private final UUID id;
    private final UUID customerId;
    private final UUID vehicleId;
    private final List<Item> items;
    private final LocalDateTime createdAt;
    private Status status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime deliveredAt;

    public ServiceOrder(UUID customerId, UUID vehicleId) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.status = Status.RECEIVED;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public enum Status {
        RECEIVED, IN_DIAGNOSIS, AWAITING_APPROVAL, IN_EXECUTION, FINISHED, DELIVERED, CANCELLED
    }

    public enum ItemType {PART, SERVICE}

    public static record Item(UUID catalogId, String description, BigDecimal unitPrice, int quantity, ItemType type) {
        public BigDecimal getTotalPrice() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void addPart(UUID partId, String name, BigDecimal currentPrice, int quantity) {
        validateModificationState();
        this.items.add(new Item(partId, name, currentPrice, quantity, ItemType.PART));
    }

    public void addService(UUID serviceId, String name, BigDecimal currentPrice) {
        validateModificationState();
        this.items.add(new Item(serviceId, name, currentPrice, 1, ItemType.SERVICE));
    }

    private void validateModificationState() {
        if (this.status != Status.RECEIVED && this.status != Status.IN_DIAGNOSIS) {
            throw new IllegalStateException("Items can only be added during RECEIVED or IN_DIAGNOSIS stages.");
        }
    }

    //Status definition
    public void approve() {
        if (this.status != Status.AWAITING_APPROVAL) {
            throw new IllegalStateException("Order must be AWAITING_APPROVAL to be approved.");
        }
        this.status = Status.IN_EXECUTION;
        this.startedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (EnumSet.of(Status.FINISHED, Status.DELIVERED).contains(this.status)) {
            throw new IllegalStateException("Cannot cancel an order that is already finished or delivered.");
        }
        this.status = Status.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void startDiagnosis() {
        if (this.status != Status.RECEIVED) {
            throw new IllegalStateException("Order must be RECEIVED to start diagnosis.");
        }
        this.status = Status.IN_DIAGNOSIS;
    }

    public void completeDiagnosis() {
        if (this.status != Status.IN_DIAGNOSIS) {
            throw new IllegalStateException("Order must be IN_DIAGNOSIS to complete diagnosis.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot complete diagnosis without adding at least one item.");
        }
        this.status = Status.AWAITING_APPROVAL;
    }

    public void finish() {
        if (this.status != Status.IN_EXECUTION) {
            throw new IllegalStateException("Order must be IN_EXECUTION to be finished.");
        }
        this.status = Status.FINISHED;
        this.finishedAt = LocalDateTime.now();
    }

    // Registra a entrega do veículo ao cliente
    public void deliver() {
        if (this.status != Status.FINISHED) {
            throw new IllegalStateException("Order must be FINISHED to be delivered.");
        }
        this.status = Status.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }


    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() { return id; }
    public Status getStatus() { return status; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
}
