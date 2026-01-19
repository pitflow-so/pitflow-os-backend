package br.com.pitflow.registry.application;

import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeleteVehicleImpTest {
    @Test
    @DisplayName("Should delete vehicle successfully")
    void shouldDeleteVehicle() {
        // Arrange
        var repository = mock(VehicleRepository.class);
        var useCase = new DeleteVehicleImp(repository);
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.of(mock(Vehicle.class)));

        // Act
        useCase.execute(id);

        // Verify
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Arrange
        var repository = mock(VehicleRepository.class);
        var useCase = new DeleteVehicleImp(repository);
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(id))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
