package br.com.pitflow.operation.application;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.GetServiceOrderByIdImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetServiceOrderByIdImpTest {

    private ServiceOrderGateway gateway;
    private GetServiceOrderByIdImp getServiceOrderById;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        getServiceOrderById = new GetServiceOrderByIdImp(gateway);
    }

    @Test
    @DisplayName("Should return service order when id exists")
    void shouldReturnServiceOrderWhenIdExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        var expectedOrder = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Reparo no motor");
        when(gateway.findById(id)).thenReturn(Optional.of(expectedOrder));

        // Act
        var result = getServiceOrderById.execute(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedOrder);

        // Verify
        verify(gateway, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when Service Order not found")
    void shouldThrowExceptionWhenServiceOrderNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getServiceOrderById.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service Order not found with ID: " + id);

        // Verify
        verify(gateway, times(1)).findById(id);
    }
}