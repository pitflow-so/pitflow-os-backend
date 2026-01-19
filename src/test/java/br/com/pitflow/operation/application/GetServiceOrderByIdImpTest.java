package br.com.pitflow.operation.application;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetServiceOrderByIdImpTest {

    private ServiceOrderRepository repository;
    private GetServiceOrderByIdImp getServiceOrderById;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        getServiceOrderById = new GetServiceOrderByIdImp(repository);
    }

    @Test
    @DisplayName("Should return service order when id exists")
    void shouldReturnServiceOrderWhenIdExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        var expectedOrder = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Reparo no motor");
        when(repository.findById(id)).thenReturn(Optional.of(expectedOrder));

        // Act
        var result = getServiceOrderById.execute(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedOrder);

        // Verify
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when Service Order not found")
    void shouldThrowExceptionWhenServiceOrderNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getServiceOrderById.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service Order not found with ID: " + id);

        // Verify
        verify(repository, times(1)).findById(id);
    }
}