package br.com.pitflow.inventory.core.entity;

import java.math.BigDecimal;
import java.util.UUID;

public class Part {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;

    public Part(String sku, String name, String description, BigDecimal price, int initialStock) {
        validateName(name);
        validatePrice(price);
        validateStock(initialStock);
        validateSku(sku);

        this.id = UUID.randomUUID();
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = initialStock;
    }

    private void validateSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Part SKU cannot be empty.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Part name cannot be empty.");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Part price must be greater than zero.");
        }
    }

    private void validateStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
    }

    public void addStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to add must be positive.");
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to remove must be positive.");
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock for part: " + name);
        }
        this.stockQuantity -= quantity;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public void setId(UUID id) { this.id = id; }
    public void setSku(String sku) {
        validateSku(sku);
        this.sku = sku;
    }

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

    public void setStockQuantity(int i) {
        validateStock(i);
        this.stockQuantity = i;
    }
}
