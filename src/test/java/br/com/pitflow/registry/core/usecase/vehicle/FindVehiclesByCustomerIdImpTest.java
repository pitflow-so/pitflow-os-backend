package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.FindVehiclesByCustomerIdImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FindVehiclesByCustomerIdImpTest {

    private VehicleGateway repository;
    private FindVehiclesByCustomerIdImp findVehiclesByCustomer;

    @BeforeEach
    void setUp() {
        repository = mock(VehicleGateway.class);
        findVehiclesByCustomer = new FindVehiclesByCustomerIdImp(repository);
    }

    @Test
    @DisplayName("Should return list of vehicles when customer has them")
    void shouldReturnListOfVehiclesWhenCustomerHasThem() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var v1 = new Vehicle(customerId, new LicensePlate("ABC1D23"), "Toyota", "Corolla", 2022);
        var v2 = new Vehicle(customerId, new LicensePlate("XYZ9G88"), "Honda", "Civic", 2021);

        when(repository.findByCustomerId(customerId)).thenReturn(List.of(v1, v2));

        // Act
        List<Vehicle> result = findVehiclesByCustomer.execute(customerId);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(Vehicle::getLicensePlate)
                .extracting(LicensePlate::value)
                .containsExactlyInAnyOrder("ABC1D23", "XYZ9G88");

        assertThat(result).allSatisfy(vehicle ->
                assertThat(vehicle.getCustomerId()).isEqualTo(customerId)
        );

        // Verify
        verify(repository, times(1)).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should return empty list when customer has no vehicles")
    void shouldReturnEmptyListWhenCustomerHasNoVehicles() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(repository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        // Act
        List<Vehicle> result = findVehiclesByCustomer.execute(customerId);

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(repository, times(1)).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should call repository with correct parameters")
    void shouldCallRepositoryWithCorrectParameters() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        // Act
        findVehiclesByCustomer.execute(customerId);

        // Assert
        verify(repository).findByCustomerId(argThat(id -> id.equals(customerId)));
    }
}