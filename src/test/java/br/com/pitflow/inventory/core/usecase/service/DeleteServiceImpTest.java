package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeleteServiceImpTest {

    private ServiceGateway gateway;
    private DeleteServiceImp deleteService;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceGateway.class);
        deleteService = new DeleteServiceImp(gateway);
    }

    @Test
    @DisplayName("Should delete service successfully")
    void shouldDeleteServiceSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var service = new Service("Mão de Obra", "Descrição", new BigDecimal("100.00"));

        when(gateway.findById(id)).thenReturn(Optional.of(service));

        // Act
        deleteService.execute(id);

        // Assert
        verify(gateway, times(1)).findById(id);
        verify(gateway, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when service not found")
    void shouldThrowExceptionWhenServiceNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deleteService.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service not found");

        // Verify
        verify(gateway, never()).deleteById(any());
    }
}