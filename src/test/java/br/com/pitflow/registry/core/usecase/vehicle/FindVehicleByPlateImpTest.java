package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindVehicleByPlateImpTest {
    private VehicleGateway gateway;
    private FindVehicleByPlateImp useCase;

    @BeforeEach void setUp() {
        gateway = mock(VehicleGateway.class);
        useCase = new FindVehicleByPlateImp(gateway);
    }

    @Test
    @DisplayName("Should return vehicle when plate exists")
    void shouldReturnVehicleWhenPlateExists() {
        // Arrange
        var plate = "ABC1D23";
        var vehicle = new Vehicle(UUID.randomUUID(), new LicensePlate(plate), "Fiat", "Uno", 2010);
        when(gateway.findByLicensePlate(any())).thenReturn(Optional.of(vehicle));

        // Act
        var result = useCase.execute(plate);

        // Assert
        assertThat(result.getLicensePlate().value()).isEqualTo(plate);

        // Verify
        verify(gateway).findByLicensePlate(any());
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void shouldThrowExceptionWhenPlateNotFound() {
        // Arrange
        when(gateway.findByLicensePlate(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute("XYZ9999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vehicle not found with plate");
    }
}