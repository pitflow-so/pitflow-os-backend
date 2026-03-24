package br.com.pitflow.operation.controller.dto;

import java.util.List;
import java.util.UUID;

public record CreateServiceOrderAllDataCommand(
        UUID customerId,
        UUID vehicleId,
        String orderDescription,
        List<ServiceOrderItemCommand> orderItems
) {
    public record ServiceOrderItemCommand(UUID catalogId, int quantity, String type){}
}
