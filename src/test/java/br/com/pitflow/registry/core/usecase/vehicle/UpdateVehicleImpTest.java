package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.controller.dto.UpdateVehicleCommand;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateVehicleImpTest {
    private VehicleGateway gateway;
    private UpdateVehicleImp updateVehicle;

    @BeforeEach void setUp() {
        gateway = mock(VehicleGateway.class);
        updateVehicle = new UpdateVehicleImp(gateway);
    }

    @Test
    @DisplayName("Should update vehicle successfully")
    void shouldUpdateVehicleSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var vehicle = new Vehicle(UUID.randomUUID(), new LicensePlate("ABC1234"), "Ford", "Ka", 2020);
        var dto = new UpdateVehicleCommand("Honda", "Civic", 2022, "XYZ9876");

        when(gateway.findById(id)).thenReturn(Optional.of(vehicle));
        when(gateway.findByLicensePlate(any())).thenReturn(Optional.empty());

        // Act
        updateVehicle.execute(id, dto);

        // Assert
        assertThat(vehicle.getModel()).isEqualTo("Civic");
        assertThat(vehicle.getBrand()).isEqualTo("Honda");
        assertThat(vehicle.getYear()).isEqualTo(2022);
        assertThat(vehicle.getLicensePlate().value()).isEqualTo("XYZ9876");

        // verify
        verify(gateway).save(vehicle);
    }

    @Test
    @DisplayName("Should throw exception when plate conflict")
    void shouldThrowExceptionWhenPlateConflict() {
        // Arrange
        UUID id = UUID.randomUUID();
        var vehicle = new Vehicle(UUID.randomUUID(), new LicensePlate("OLD1111"), "Brand", "Model", 2000);
        var anotherVehicle = new Vehicle(UUID.randomUUID(), new LicensePlate("NEW2222"), "Other", "Other", 2010);
        var dto = new UpdateVehicleCommand("Brand", "Model", 2000, "NEW2222");

        when(gateway.findById(id)).thenReturn(Optional.of(vehicle));
        when(gateway.findByLicensePlate(new LicensePlate("NEW2222"))).thenReturn(Optional.of(anotherVehicle));

        // Act & Assert
        assertThatThrownBy(() -> updateVehicle.execute(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in use");
    }
}
