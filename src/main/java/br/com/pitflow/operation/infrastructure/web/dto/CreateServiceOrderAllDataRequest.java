package br.com.pitflow.operation.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public record CreateServiceOrderAllDataRequest (
        UUID customerId,
        UUID vehicleId,
        String orderDescription,
        List<ServiceOrderItemRequest> orderItems
) {
    public record ServiceOrderItemRequest(UUID catalogId, int quantity, String type){}
}
