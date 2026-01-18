package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.aplication.AddVehicleImp;
import br.com.pitflow.registry.aplication.dto.AddVehicleDto;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
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
    private VehicleRepository vehicleRepository;
    private CustomerRepository customerRepository;
    private AddVehicleImp addVehicle;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        customerRepository = mock(CustomerRepository.class);
        addVehicle = new AddVehicleImp(vehicleRepository, customerRepository);
    }

    @Test
    @DisplayName("Should add vehicle with success to an existing customer")
    void shouldAddVehicleWithSuccess() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleDto(customerId, "ABC1D23", "Toyota", "Corolla", 2024);
        var customer = new Customer("Rafael Moreira", new CpfCnpj("06678477073"), "11996195936");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByLicensePlate(any(LicensePlate.class))).thenReturn(Optional.empty());

        // Act
        Vehicle result = addVehicle.execute(dto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("ABC1D23", result.getLicensePlate().value());

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleDto(customerId, "ABC1D23", "Toyota", "Corolla", 2024);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class, () -> addVehicle.execute(dto));
        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());

        verifyNoInteractions(vehicleRepository);
    }

    @Test
    @DisplayName("Should throw exception when license plate already exists")
    void shouldThrowExceptionWhenLicensePlateAlreadyExists() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleDto(customerId, "ABC1D23", "Toyota", "Corolla", 2024);
        var customer = new Customer("Rafael Moreira", new CpfCnpj("06678477073"), "11996195936");
        var existingVehicle = new Vehicle(customerId, new LicensePlate("ABC1D23"), "Fiat", "Uno", 2010);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByLicensePlate(any(LicensePlate.class))).thenReturn(Optional.of(existingVehicle));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> addVehicle.execute(dto));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when license plate is invalid")
    void shouldThrowExceptionWhenLicensePlateIsInvalid() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        var dto = new AddVehicleDto(customerId, "INVALID", "Toyota", "Corolla", 2024);
        var customer = new Customer("Rafael Moreira", new CpfCnpj("06678477073"), "11996195936");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> addVehicle.execute(dto));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}
