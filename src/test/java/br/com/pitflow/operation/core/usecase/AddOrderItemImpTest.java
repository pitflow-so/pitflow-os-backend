package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.entity.ServiceOrder.ItemType;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private PartGateway partGateway;
    private ServiceGateway serviceGateway;
    private AddOrderItemImp addOrderItem;

    @BeforeEach
    void setUp() {
        serviceOrderGateway = mock(ServiceOrderGateway.class);
        partGateway = mock(PartGateway.class);
        serviceGateway = mock(ServiceGateway.class);
        addOrderItem = new AddOrderItemImp(serviceOrderGateway, partGateway, serviceGateway);
    }

    @Test
    @DisplayName("Should add part to order successfully")
    void shouldAddPartToOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Problemas com o filtro de ar");
        var part = new Part("SKU", "Filtro", "Filtro de ar automotivo",new BigDecimal("50.0"), 2);
        part.setId(partId);

        when(serviceOrderGateway.findById(osId)).thenReturn(Optional.of(os));
        when(partGateway.findById(partId)).thenReturn(Optional.of(part));

        // Act
        addOrderItem.execute(new AddOrderItemCommand(osId, partId, 2, ItemType.PART.name()));

        // Assert
        assertThat(os.getItems()).hasSize(1);
        assertThat(os.getTotalAmount()).isEqualByComparingTo(new BigDecimal("100.0"));

        // Verify
        verify(serviceOrderGateway).save(os);
    }

    @Test
    @DisplayName("Should fail if stock is insufficient")
    void shouldFailIfStockIsInsufficient() {
        // Arrange
        UUID osId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        var part = new Part("SKU", "Pneu", "pneu pirelli 175 65 r14", new BigDecimal("500.0"), 2);
        part.setId(partId);

        when(serviceOrderGateway.findById(osId)).thenReturn(Optional.of(mock(ServiceOrder.class)));
        when(partGateway.findById(partId)).thenReturn(Optional.of(part));

        //Act
        // Tak 4 but there is only two in the inventory
        var dto = new AddOrderItemCommand(osId, partId, 4, ItemType.PART.name());

        // Assert
        assertThatThrownBy(() -> addOrderItem.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should add part to order and decrement inventory successfully")
    void shouldAddPartAndDecrementInventory() {
        // Arrange
        UUID osId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Reparo suspensão");
        var part = new Part("SKU-001", "Amortecedor", "Desc", new BigDecimal("500.0"), 10);
        part.setId(partId);

        when(serviceOrderGateway.findById(osId)).thenReturn(Optional.of(os));
        when(partGateway.findById(partId)).thenReturn(Optional.of(part));

        // Act
        addOrderItem.execute(new AddOrderItemCommand(osId, partId, 2, ItemType.PART.name()));

        // Assert
        assertThat(os.getItems()).hasSize(1);
        assertThat(part.getStockQuantity()).isEqualTo(8);

        // Verify
        verify(partGateway).save(part);
        verify(serviceOrderGateway).save(os);
    }
}