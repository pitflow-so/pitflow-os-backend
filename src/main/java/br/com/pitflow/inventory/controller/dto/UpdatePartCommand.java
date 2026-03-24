package br.com.pitflow.inventory.controller.dto;

import java.math.BigDecimal;

public record UpdatePartCommand(String sku, String name, String description, BigDecimal price, int stockQuantity) {
}
