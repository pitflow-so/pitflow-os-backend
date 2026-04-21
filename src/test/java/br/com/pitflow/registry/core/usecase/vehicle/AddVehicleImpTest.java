package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.valueObject.Email;
import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.controller.dto.AddVehicleCommand;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class AddVehicleImpTest {
    private VehicleGateway vehicleGateway;
    private CustomerGateway customerGateway;
    private AddVehicleImp addVehicle;

    @BeforeEach
    void setUp() {
        vehicleGateway = mock(VehicleGateway.class);
        customerGateway = mock(CustomerGateway.class);
        addVehicle = new AddVehicleImp(vehicleGateway, customerGateway);
    }

    @Test
    @DisplayName("Should add vehicle with success to an existing customer")
    void shouldAddVehicleWithSuccess() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleCommand(customerId, "ABC1D23", "Toyota", "Corolla", 2024);
        var customer = new Customer(
                "Rafael Moreira",
                "11996195936",
                new Email("rafael@gmail.com"),
                new CpfCnpj("06678477073")
        );

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleGateway.findByLicensePlate(any(LicensePlate.class))).thenReturn(Optional.empty());

        // Act
        Vehicle result = addVehicle.execute(dto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("ABC1D23", result.getLicensePlate().value());

        verify(vehicleGateway, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleCommand(customerId, "ABC1D23", "Toyota", "Corolla", 2024);

        when(customerGateway.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> addVehicle.execute(dto));
        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());

        verifyNoInteractions(vehicleGateway);
    }

    @Test
    @DisplayName("Should throw exception when license plate already exists")
    void shouldThrowExceptionWhenLicensePlateAlreadyExists() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleCommand(customerId, "ABC1D23", "Toyota", "Corolla", 2024);
        var customer = new Customer(
                "Rafael Moreira",
                "11996195936",
                new Email("rafael@gmail.com"),
                new CpfCnpj("06678477073")
        );
        var existingVehicle = new Vehicle(customerId, new LicensePlate("ABC1D23"), "Fiat", "Uno", 2010);

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleGateway.findByLicensePlate(any(LicensePlate.class))).thenReturn(Optional.of(existingVehicle));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> addVehicle.execute(dto));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(vehicleGateway, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when license plate is invalid")
    void shouldThrowExceptionWhenLicensePlateIsInvalid() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleCommand(customerId, "INVALID", "Toyota", "Corolla", 2024);
        var customer = new Customer(
                "Rafael Moreira",
                "11996195936",
                new Email("rafael@gmail.com"),
                new CpfCnpj("06678477073")
        );

        when(customerGateway.findById(customerId)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> addVehicle.execute(dto));
        verify(vehicleGateway, never()).save(any(Vehicle.class));
    }
}
