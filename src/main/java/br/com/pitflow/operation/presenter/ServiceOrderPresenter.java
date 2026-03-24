package br.com.pitflow.operation.presenter;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.usecase.outputData.ExecutionTimeMetrics;
import br.com.pitflow.operation.core.usecase.outputData.OrderDurationMetrics;
import br.com.pitflow.operation.presenter.dto.ServiceOrderItemResponse;
import br.com.pitflow.operation.presenter.dto.ExecutionTimeMetricsResponse;
import br.com.pitflow.operation.presenter.dto.OrderDurationResponse;
import br.com.pitflow.operation.presenter.dto.ServiceOrderResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceOrderPresenter {
    private ServiceOrderPresenter(){}

    public static ServiceOrderResponse toResponse(ServiceOrder entity){
        return new ServiceOrderResponse(
                entity.getId(),
                entity.getCustomerId(),
                entity.getVehicleId(),
                entity.getDescription(),
                entity.getStatus().name(),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                entity.getFinishedAt(),
                mapItems(entity.getItems()),
                entity.getCancelledAt() != null
                        ? String.format("Cancelado em %s: %s", entity.getCancelledAt(), entity.getCancellationDescription())
                        : entity.getCancellationDescription()
        );
    }

    private static List<ServiceOrderItemResponse> mapItems(List<ServiceOrder.Item> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(item -> new ServiceOrderItemResponse(
                        item.catalogId(),
                        item.description(),
                        item.unitPrice(),
                        item.quantity(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());
    }

    public static ExecutionTimeMetricsResponse toResponse(ExecutionTimeMetrics metrics) {
        return new ExecutionTimeMetricsResponse(
                metrics.averageMinutes(),
                metrics.formatted()
        );
    }

    public static OrderDurationResponse toResponse(OrderDurationMetrics metrics) {
        return new OrderDurationResponse(
                metrics.serviceOrderId(),
                metrics.durationInMinutes(),
                metrics.formattedDuration(),
                metrics.isStillRunning()
        );
    }
}
