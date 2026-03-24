package br.com.pitflow.operation.infrastructure.persistence.entity;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "service_orders")
public class ServiceOrderJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceOrder.Status status;

    @ElementCollection(fetch = FetchType.EAGER) // Carrega os itens junto com a ordem de serviço
    @CollectionTable(
            name = "service_order_items",
            joinColumns = @JoinColumn(name = "service_order_id")
    )
    private List<OrderItemJpa> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "diagnosis_started_at")
    private LocalDateTime diagnosisStartedAt;

    @Column(name = "execution_started_at")
    private LocalDateTime executionStartedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_description", columnDefinition = "TEXT")
    private String cancellationDescription;

    public ServiceOrderJpa() {
    }

    public ServiceOrderJpa(UUID id, UUID customerId, UUID vehicleId, String description, ServiceOrder.Status status,
                           List<OrderItemJpa> items, LocalDateTime createdAt, LocalDateTime diagnosisStartedAt,
                           LocalDateTime executionStartedAt, LocalDateTime finishedAt, LocalDateTime deliveredAt,
                           LocalDateTime cancelledAt, String cancellationDescription) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.status = status;
        this.items = items;
        this.createdAt = createdAt;
        this.diagnosisStartedAt = diagnosisStartedAt;
        this.executionStartedAt = executionStartedAt;
        this.finishedAt = finishedAt;
        this.deliveredAt = deliveredAt;
        this.cancelledAt = cancelledAt;
        this.cancellationDescription = cancellationDescription;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public UUID getVehicleId() { return vehicleId; }
    public void setVehicleId(UUID vehicleId) { this.vehicleId = vehicleId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ServiceOrder.Status getStatus() { return status; }
    public void setStatus(ServiceOrder.Status status) { this.status = status; }

    public List<OrderItemJpa> getItems() { return items; }
    public void setItems(List<OrderItemJpa> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDiagnosisStartedAt() { return diagnosisStartedAt; }
    public void setDiagnosisStartedAt(LocalDateTime diagnosisStartedAt) { this.diagnosisStartedAt = diagnosisStartedAt; }

    public LocalDateTime getExecutionStartedAt() { return executionStartedAt; }
    public void setExecutionStartedAt(LocalDateTime executionStartedAt) { this.executionStartedAt = executionStartedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationDescription() { return cancellationDescription; }
    public void setCancellationDescription(String cancellationDescription) { this.cancellationDescription = cancellationDescription; }
}