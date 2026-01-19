package br.com.pitflow.operation.infrastructure.persistence.mapper;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.infrastructure.persistence.entity.OrderItemJpa;

public class OrderItemMapper {

    private OrderItemMapper() {}

    // Converte do Domínio para JPA
    public static OrderItemJpa toJpa(ServiceOrder.Item domainItem) {
        if (domainItem == null) return null;

        return new OrderItemJpa(
                domainItem.catalogId(),
                domainItem.description(),
                domainItem.unitPrice(),
                domainItem.quantity(),
                domainItem.type()
        );
    }

    // Converte do JPA para Domínio (Record)
    public static ServiceOrder.Item toDomain(OrderItemJpa jpaItem) {
        if (jpaItem == null) return null;

        return new ServiceOrder.Item(
                jpaItem.getCatalogId(),
                jpaItem.getDescription(),
                jpaItem.getUnitPrice(),
                jpaItem.getQuantity(),
                jpaItem.getType()
        );
    }
}