package br.com.pitflow.operation.presenter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceOrderResponse(
        UUID id,
        UUID customerId,
        UUID vehicleId,
        String description,
        String status,

        BigDecimal totalAmount,

        LocalDateTime createdAt,
        LocalDateTime finishedAt,

        List<ServiceOrderItemResponse> items,

        String cancellationDescription
) {}