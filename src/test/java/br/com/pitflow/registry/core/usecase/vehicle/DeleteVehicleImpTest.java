package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteVehicleImpTest {
    @Test
    @DisplayName("Should delete vehicle successfully")
    void shouldDeleteVehicle() {
        // Arrange
        var gateway = mock(VehicleGateway.class);
        var useCase = new DeleteVehicleImp(gateway);
        UUID id = UUID.randomUUID();

        when(gateway.findById(id)).thenReturn(Optional.of(mock(Vehicle.class)));

        // Act
        useCase.execute(id);

        // Verify
        verify(gateway).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Arrange
        var gateway = mock(VehicleGateway.class);
        var useCase = new DeleteVehicleImp(gateway);
        UUID id = UUID.randomUUID();

        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
