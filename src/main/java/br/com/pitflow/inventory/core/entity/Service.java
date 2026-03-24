package br.com.pitflow.inventory.core.entity;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service (Catalog of Labor/Mão de Obra)
 * Represents a standard service provided by the workshop (e.g., Oil Change, Balancing).
 */
public class Service {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;

    public Service(String name, String description, BigDecimal price) {
        validateName(name);
        validatePrice(price);

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Service name cannot be empty.");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Service price cannot be negative.");
        }
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        validatePrice(price);
        this.price = price;
    }
}
