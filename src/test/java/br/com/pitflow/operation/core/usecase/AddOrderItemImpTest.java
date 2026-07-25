package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.InventoryGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddOrderItemImpTest {
    private ServiceOrderGateway serviceOrderGateway;
    private InventoryGateway inventoryGateway;
    private AddOrderItemImp addOrderItem;

    @BeforeEach
    void setUp() {
        serviceOrderGateway = mock(ServiceOrderGateway.class);
        inventoryGateway = mock(InventoryGateway.class);
        addOrderItem = new AddOrderItemImp(serviceOrderGateway, inventoryGateway);
    }

    @Test
    void shouldReservePartAndAddItToOrder() {
        var orderId = UUID.randomUUID();
        var partId = UUID.randomUUID();
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Filtro de ar");
        var part = new InventoryGateway.CatalogItem(partId, "Filtro", new BigDecimal("50.00"));

        when(serviceOrderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryGateway.reservePart(partId, 2)).thenReturn(part);

        addOrderItem.execute(new AddOrderItemCommand(
                orderId, partId, 2, ServiceOrder.ItemType.PART.name()));

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("100.00");
        verify(inventoryGateway).reservePart(partId, 2);
        verify(serviceOrderGateway).save(order);
    }

    @Test
    void shouldAddServiceFromInventoryCatalog() {
        var orderId = UUID.randomUUID();
        var serviceId = UUID.randomUUID();
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Alinhamento");
        var service = new InventoryGateway.CatalogItem(
                serviceId, "Alinhamento", new BigDecimal("150.00"));

        when(serviceOrderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryGateway.findService(serviceId)).thenReturn(service);

        addOrderItem.execute(new AddOrderItemCommand(
                orderId, serviceId, 1, ServiceOrder.ItemType.SERVICE.name()));

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("150.00");
        verify(inventoryGateway).findService(serviceId);
        verify(serviceOrderGateway).save(order);
    }

    @Test
    void shouldNotSaveOrderWhenInventoryReservationFails() {
        var orderId = UUID.randomUUID();
        var partId = UUID.randomUUID();
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Suspensão");

        when(serviceOrderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(inventoryGateway.reservePart(partId, 4))
                .thenThrow(new IllegalStateException("Insufficient stock"));

        var command = new AddOrderItemCommand(
                orderId, partId, 4, ServiceOrder.ItemType.PART.name());

        assertThatThrownBy(() -> addOrderItem.execute(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }
}
