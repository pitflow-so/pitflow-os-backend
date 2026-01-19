package br.com.pitflow.operation.application;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliverOrderImpTest {

    private ServiceOrderRepository repository;
    private DeliverOrderImp deliverOrder;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        deliverOrder = new DeliverOrderImp(repository);
    }

    @Test
    @DisplayName("Should deliver order successfully when it is finished")
    void shouldDeliverOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Manutenção preventiva");

        // Advante to FINISHED status
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Serviço", new BigDecimal("100"));
        os.completeDiagnosis();
        os.approve();
        os.finish();

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act
        deliverOrder.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.DELIVERED);
        assertThat(os.getDeliveredAt()).isNotNull();
        verify(repository).save(os);
    }

    @Test
    @DisplayName("Should fail to deliver if order is not in FINISHED state")
    void shouldFailIfInvalidState() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Conserto");

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> deliverOrder.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order must be FINISHED to be delivered.");
    }
}