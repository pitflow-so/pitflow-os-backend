package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FindVehicleByIdImpTest {

    private VehicleGateway gateway;
    private FindVehicleByIdImp findVehicleById;

    @BeforeEach
    void setUp() {
        gateway = mock(VehicleGateway.class);
        findVehicleById = new FindVehicleByIdImp(gateway);
    }

    @Test
    @DisplayName("Should return vehicle when ID exists")
    void shouldReturnVehicleWhenIdExists() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        var vehicle = new Vehicle(customerId, new LicensePlate("ABC1D23"), "Volkswagen", "Polo", 2023);
        vehicle.setId(vehicleId);

        when(gateway.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act
        Vehicle result = findVehicleById.execute(vehicleId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(vehicleId);
        assertThat(result.getLicensePlate().value()).isEqualTo("ABC1D23");
        verify(gateway, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void shouldThrowExceptionWhenVehicleNotFound() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        when(gateway.findById(vehicleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findVehicleById.execute(vehicleId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vehicle not found with ID: " + vehicleId);

        verify(gateway, times(1)).findById(vehicleId);
    }
}
