package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FindVehicleByIdImpTest {

    private VehicleRepository repository;
    private FindVehicleByIdImp findVehicleById;

    @BeforeEach
    void setUp() {
        repository = mock(VehicleRepository.class);
        findVehicleById = new FindVehicleByIdImp(repository);
    }

    @Test
    @DisplayName("Should return vehicle when ID exists")
    void shouldReturnVehicleWhenIdExists() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        var vehicle = new Vehicle(customerId, new LicensePlate("ABC1D23"), "Volkswagen", "Polo", 2023);
        vehicle.setId(vehicleId);

        when(repository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act
        Vehicle result = findVehicleById.execute(vehicleId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(vehicleId);
        assertThat(result.getLicensePlate().value()).isEqualTo("ABC1D23");
        verify(repository, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        when(repository.findById(vehicleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findVehicleById.execute(vehicleId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehicle not found with ID: " + vehicleId);

        verify(repository, times(1)).findById(vehicleId);
    }
}
