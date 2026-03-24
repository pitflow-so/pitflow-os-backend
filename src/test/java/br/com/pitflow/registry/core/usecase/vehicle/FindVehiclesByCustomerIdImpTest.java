package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindVehiclesByCustomerIdImpTest {

    private VehicleGateway gateway;
    private FindVehiclesByCustomerIdImp findVehiclesByCustomer;

    @BeforeEach
    void setUp() {
        gateway = mock(VehicleGateway.class);
        findVehiclesByCustomer = new FindVehiclesByCustomerIdImp(gateway);
    }

    @Test
    @DisplayName("Should return list of vehicles when customer has them")
    void shouldReturnListOfVehiclesWhenCustomerHasThem() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var v1 = new Vehicle(customerId, new LicensePlate("ABC1D23"), "Toyota", "Corolla", 2022);
        var v2 = new Vehicle(customerId, new LicensePlate("XYZ9G88"), "Honda", "Civic", 2021);

        when(gateway.findByCustomerId(customerId)).thenReturn(List.of(v1, v2));

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
        verify(gateway, times(1)).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should return empty list when customer has no vehicles")
    void shouldReturnEmptyListWhenCustomerHasNoVehicles() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(gateway.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        // Act
        List<Vehicle> result = findVehiclesByCustomer.execute(customerId);

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(gateway, times(1)).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should call repository with correct parameters")
    void shouldCallRepositoryWithCorrectParameters() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        // Act
        findVehiclesByCustomer.execute(customerId);

        // Assert
        verify(gateway).findByCustomerId(argThat(id -> id.equals(customerId)));
    }
}