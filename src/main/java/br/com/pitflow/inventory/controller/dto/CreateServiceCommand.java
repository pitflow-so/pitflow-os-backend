package br.com.pitflow.inventory.controller.dto;

import java.math.BigDecimal;

public record CreateServiceCommand(String name, String description, BigDecimal price) {
}
