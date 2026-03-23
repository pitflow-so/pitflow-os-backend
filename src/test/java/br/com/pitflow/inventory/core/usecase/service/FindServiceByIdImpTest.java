package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FindServiceByIdImpTest {

    private ServiceGateway gateway;
    private FindServiceByIdImp findServiceById;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceGateway.class);
        findServiceById = new FindServiceByIdImp(gateway);
    }

    @Test
    @DisplayName("Should return service when ID exists")
    void shouldReturnServiceWhenIdExists() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        var service = new Service("Troca de Óleo", "Substituição de óleo e filtro", new BigDecimal("150.00"));
        service.setId(expectedId);

        when(gateway.findById(expectedId)).thenReturn(Optional.of(service));

        // Act
        Service result = findServiceById.execute(expectedId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.getName()).isEqualTo("Troca de Óleo");
        verify(gateway, times(1)).findById(expectedId);
    }

    @Test
    @DisplayName("Should throw exception when ID not found")
    void shouldThrowExceptionWhenServiceNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findServiceById.execute(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Service not found");

        verify(gateway, times(1)).findById(id);
    }
}