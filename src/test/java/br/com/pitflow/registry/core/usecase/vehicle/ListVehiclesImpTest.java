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
import static org.mockito.Mockito.*;

class ListVehiclesImpTest {

    private VehicleGateway gateway;
    private ListVehiclesImp listVehicles;

    @BeforeEach
    void setUp() {
        gateway = mock(VehicleGateway.class);
        listVehicles = new ListVehiclesImp(gateway);
    }

    @Test
    @DisplayName("Should return all vehicles")
    void shouldReturnAllVehicles() {
        // Arrange
        var v1 = new Vehicle(UUID.randomUUID(), new LicensePlate("PLK0J11"), "Chevrolet", "Onix", 2022);
        var v2 = new Vehicle(UUID.randomUUID(), new LicensePlate("KLI9090"), "Renault", "Sandero", 2019);

        when(gateway.findAll()).thenReturn(List.of(v1, v2));

        // Act
        List<Vehicle> result = listVehicles.execute();

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(v1, v2);

        verify(gateway, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no vehicles exist")
    void shouldReturnEmptyListWhenNoVehiclesExist() {
        // Arrange
        when(gateway.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Vehicle> result = listVehicles.execute();

        // Assert
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(gateway, times(1)).findAll();
    }
}