package br.com.pitflow.operation.controller.dto;

import java.util.UUID;

public record CreateServiceOrderCommand(UUID customerId, UUID vehicleId, String description) {
}
