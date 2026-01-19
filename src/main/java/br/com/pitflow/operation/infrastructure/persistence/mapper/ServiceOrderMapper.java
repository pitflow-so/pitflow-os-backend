package br.com.pitflow.operation.infrastructure.persistence.mapper;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.infrastructure.persistence.entity.ServiceOrderJpa;

import java.util.stream.Collectors;

public class ServiceOrderMapper {

    private ServiceOrderMapper(){}

    public static ServiceOrderJpa toJpa(ServiceOrder domain) {
        if (domain == null) return null;

        var itemsJpa = domain.getItems().stream()
                .map(OrderItemMapper::toJpa)
                .collect(Collectors.toList());

        return new ServiceOrderJpa(
                domain.getId(),
                domain.getCustomerId(),
                domain.getVehicleId(),
                domain.getDescription(),
                domain.getStatus(),
                itemsJpa,
                domain.getCreatedAt(),
                domain.getDiagnosisStartedAt(),
                domain.getExecutionStartedAt(),
                domain.getFinishedAt(),
                domain.getDeliveredAt(),
                domain.getCancelledAt(),
                domain.getCancellationDescription()
        );
    }

    public static ServiceOrder toDomain(ServiceOrderJpa jpa) {
        if (jpa == null) return null;

        ServiceOrder domain = new ServiceOrder(
                jpa.getCustomerId(),
                jpa.getVehicleId(),
                jpa.getDescription()
        );

        domain.setId(jpa.getId());
        domain.reconstituteStatus(jpa.getStatus());
        domain.reconstituteDiagnosisStartedAt(jpa.getDiagnosisStartedAt());
        domain.reconstituteExecutionStartedAt(jpa.getExecutionStartedAt());
        domain.reconstituteFinishedAt(jpa.getFinishedAt());
        domain.reconstituteDeliveredAt(jpa.getDeliveredAt());
        domain.reconstituteCancelledAt(jpa.getCancelledAt());
        domain.reconstituteCancellationDescription(jpa.getCancellationDescription());

        var domainItems = jpa.getItems().stream()
                .map(OrderItemMapper::toDomain)
                .collect(Collectors.toList());

        domain.reconstituteItems(domainItems);

        return domain;
    }
}
