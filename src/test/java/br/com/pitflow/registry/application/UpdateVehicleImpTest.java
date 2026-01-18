package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.aplication.UpdateVehicleImp;
import br.com.pitflow.registry.aplication.dto.UpdateVehicleDto;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateVehicleImpTest {
    private VehicleRepository repository;
    private UpdateVehicleImp updateVehicle;

    @BeforeEach void setUp() {
        repository = mock(VehicleRepository.class);
        updateVehicle = new UpdateVehicleImp(repository);
    }

    @Test
    @DisplayName("Should update vehicle successfully")
    void shouldUpdateVehicleSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var vehicle = new Vehicle(UUID.randomUUID(), new LicensePlate("ABC1234"), "Ford", "Ka", 2020);
        var dto = new UpdateVehicleDto("Honda", "Civic", 2022, "XYZ9876");

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(repository.findByLicensePlate(any())).thenReturn(Optional.empty());

        // Act
        updateVehicle.execute(id, dto);

        // Assert
        assertThat(vehicle.getModel()).isEqualTo("Civic");
        assertThat(vehicle.getBrand()).isEqualTo("Honda");
        assertThat(vehicle.getYear()).isEqualTo(2022);
        assertThat(vehicle.getLicensePlate().value()).isEqualTo("XYZ9876");

        // verify
        verify(repository).save(vehicle);
    }

    @Test
    @DisplayName("Should throw exception when plate conflict")
    void shouldThrowExceptionWhenPlateConflict() {
        // Arrange
        UUID id = UUID.randomUUID();
        var vehicle = new Vehicle(UUID.randomUUID(), new LicensePlate("OLD1111"), "Brand", "Model", 2000);
        var anotherVehicle = new Vehicle(UUID.randomUUID(), new LicensePlate("NEW2222"), "Other", "Other", 2010);
        var dto = new UpdateVehicleDto("Brand", "Model", 2000, "NEW2222");

        when(repository.findById(id)).thenReturn(Optional.of(vehicle));
        when(repository.findByLicensePlate(new LicensePlate("NEW2222"))).thenReturn(Optional.of(anotherVehicle));

        // Act & Assert
        assertThatThrownBy(() -> updateVehicle.execute(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in use");
    }
}
