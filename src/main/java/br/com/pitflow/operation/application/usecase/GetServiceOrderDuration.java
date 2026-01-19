package br.com.pitflow.operation.application.usecase;

import br.com.pitflow.operation.infrastructure.api.dto.OrderDurationResponse;

import java.util.UUID;

public interface GetServiceOrderDuration {
    OrderDurationResponse execute(UUID serviceOrderId);
}
