package br.com.pitflow.operation.application;

import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;
import br.com.pitflow.operation.application.dto.AddOrderItemDto;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.ServiceOrder.ItemType;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddOrderItemImpTest {
    private ServiceOrderRepository osRepository;
    private PartRepository partRepository;
    private ServiceRepository serviceRepository;
    private AddOrderItemImp addOrderItem;

    @BeforeEach
    void setUp() {
        osRepository = mock(ServiceOrderRepository.class);
        partRepository = mock(PartRepository.class);
        serviceRepository = mock(ServiceRepository.class);
        addOrderItem = new AddOrderItemImp(osRepository, partRepository, serviceRepository);
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

        when(osRepository.findById(osId)).thenReturn(Optional.of(os));
        when(partRepository.findById(partId)).thenReturn(Optional.of(part));

        // Act
        addOrderItem.execute(new AddOrderItemDto(osId, partId, 2, ItemType.PART));

        // Assert
        assertThat(os.getItems()).hasSize(1);
        assertThat(os.getTotalAmount()).isEqualByComparingTo(new BigDecimal("100.0"));

        // Verify
        verify(osRepository).save(os);
    }

    @Test
    @DisplayName("Should fail if stock is insufficient")
    void shouldFailIfStockIsInsufficient() {
        // Arrange
        UUID osId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        var part = new Part("SKU", "Pneu", "pneu pirelli 175 65 r14", new BigDecimal("500.0"), 2);
        part.setId(partId);

        when(osRepository.findById(osId)).thenReturn(Optional.of(mock(ServiceOrder.class)));
        when(partRepository.findById(partId)).thenReturn(Optional.of(part));

        //Act
        // Tak 4 but there is only two in the inventory
        var dto = new AddOrderItemDto(osId, partId, 4, ItemType.PART);

        // Assert
        assertThatThrownBy(() -> addOrderItem.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }
}