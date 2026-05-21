package br.com.pitflow.operation.infrastructure.persistence.entity;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public class OrderItemJpa {

    @Column(name = "catalog_id", nullable = false)
    private UUID catalogId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ServiceOrder.ItemType type;

    // Construtor padrão exigido pelo JPA
    public OrderItemJpa() {
    }

    // Construtor completo para facilitar o mapeamento
    public OrderItemJpa(UUID catalogId, String description, BigDecimal unitPrice, int quantity, ServiceOrder.ItemType type) {
        this.catalogId = catalogId;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.type = type;
    }

    // Getters e Setters Manuais
    public UUID getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(UUID catalogId) {
        this.catalogId = catalogId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ServiceOrder.ItemType getType() {
        return type;
    }

    public void setType(ServiceOrder.ItemType type) {
        this.type = type;
    }
}