package br.com.pitflow.operation.presenter.dto;

import br.com.pitflow.operation.core.entity.ServiceOrder.Status;
import br.com.pitflow.operation.infrastructure.web.dto.ServiceOrderItemResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ServiceOrderResponse(
        UUID id,
        UUID customerId,
        UUID vehicleId,
        String description,
        Status status,

        BigDecimal totalAmount,

        LocalDateTime createdAt,
        LocalDateTime finishedAt,

        List<ServiceOrderItemResponse> items,

        String cancellationDescription
) {}