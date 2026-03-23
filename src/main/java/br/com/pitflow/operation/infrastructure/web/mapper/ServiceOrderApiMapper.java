package br.com.pitflow.operation.infrastructure.web.mapper;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.infrastructure.web.dto.ServiceOrderItemResponse;
import br.com.pitflow.operation.presenter.dto.ServiceOrderResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceOrderApiMapper {

    private ServiceOrderApiMapper() {}

    public static ServiceOrderResponse toResponse(ServiceOrder domain) {
        if (domain == null) {
            return null;
        }

        return new ServiceOrderResponse(
                domain.getId(),
                domain.getCustomerId(),
                domain.getVehicleId(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getTotalAmount(),
                domain.getCreatedAt(),
                domain.getFinishedAt(),
                mapItems(domain.getItems()),
                domain.getCancelledAt() != null
                        ? String.format("Cancelado em %s: %s", domain.getCancelledAt(), domain.getCancellationDescription())
                        : domain.getCancellationDescription()
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
}
