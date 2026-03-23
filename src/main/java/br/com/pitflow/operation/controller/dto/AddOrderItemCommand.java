package br.com.pitflow.operation.controller.dto;

import java.util.UUID;

public record AddOrderItemCommand(UUID serviceOrderId, UUID catalogId, int quantity, String type) {
}
