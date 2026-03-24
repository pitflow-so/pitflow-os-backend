package br.com.pitflow.inventory.controller.dto;

import java.math.BigDecimal;

public record UpdateServiceCommand(String name, String description, BigDecimal price) {
}
