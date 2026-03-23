package br.com.pitflow.registry.core.entity;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.common.valueobject.LicensePlate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomerTest {

    @Test
    @DisplayName("Should create a customer with a vehicle successfully")
    void shouldCreateCustomerWithVehicle() {
        // Arrange
        var document = new CpfCnpj("066.784.770-73");
        var customer = new Customer("Jão Santos", document, "11999999999");
        customer.setId(UUID.randomUUID());

        var plate = new LicensePlate("ABC1D23");
        var vehicle = new Vehicle(customer.getId(), plate, "Chevrolet", "Chevette", 1973);
        vehicle.setId(UUID.randomUUID());

        // Act
        customer.addVehicle(vehicle);

        // Assert
        assertThat(customer.getName()).isEqualTo("Jão Santos");
        assertThat(customer.getDocument().value()).isEqualTo("06678477073");
        assertThat(customer.getPhone()).isEqualTo("11999999999");
        assertThat(customer.getVehicles()).hasSize(1);
        assertThat(customer.getVehicles().getFirst().getLicensePlate().value()).isEqualTo("ABC1D23");
    }

    @Test
    @DisplayName("Should throw exception when creating customer with empty name")
    void shouldThrowExceptionForEmptyName() {
        var document = new CpfCnpj("066.784.770-73");

        assertThatThrownBy(() -> new Customer("", document, "11999999999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer name cannot be empty.");
    }

    @Test
    @DisplayName("Should not allow external modification of vehicle list")
    void shouldProtectVehicleListFromExternalModification() {
        // Arrange
        var customer = new Customer("Jose Maria", new CpfCnpj("06678477073"), "11999999999");
        var vehicle = new Vehicle(customer.getId() ,new LicensePlate("ABC1234"), "Ford", "Ka", 2020);

        // Act & Assert
        // returns an unmodifiable list
        var vehicles = customer.getVehicles();

        assertThatThrownBy(() -> vehicles.add(vehicle))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
