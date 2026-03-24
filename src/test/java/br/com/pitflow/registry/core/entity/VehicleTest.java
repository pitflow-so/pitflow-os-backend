package br.com.pitflow.registry.core.entity;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VehicleTest {

    private final UUID customerId = UUID.randomUUID();
    private final LicensePlate validPlate = new LicensePlate("ABC1234");

    @Test
    @DisplayName("Should create a valid vehicle")
    void shouldCreateValidVehicle() {
        // Act
        Vehicle vehicle = new Vehicle(customerId, validPlate, "Toyota", "Corolla", 2024);

        // Assert
        assertNotNull(vehicle.getId());
        assertEquals(customerId, vehicle.getCustomerId());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2024, vehicle.getYear());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for invalid brand")
    void shouldThrowExceptionForInvalidBrand(String invalidBrand) {
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle(customerId, validPlate, invalidBrand, "Corolla", 2024)
        );
    }

    @Test
    @DisplayName("Should throw exception for null brand")
    void shouldThrowExceptionForNullBrand() {
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle(customerId, validPlate, null, "Corolla", 2024)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for invalid model")
    void shouldThrowExceptionForInvalidModel(String invalidModel) {
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle(customerId, validPlate, "Toyota", invalidModel, 2024)
        );
    }

    @Test
    @DisplayName("Should throw exception for year too old")
    void shouldThrowExceptionForYearTooOld() {
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle(customerId, validPlate, "Toyota", "Corolla", 1899)
        );
    }

    @Test
    @DisplayName("Should throw exception for year too far in future")
    void shouldThrowExceptionForYearTooFarInFuture() {
        int nextYearPlusTwo = LocalDate.now().getYear() + 2;
        assertThrows(IllegalArgumentException.class, () ->
                new Vehicle(customerId, validPlate, "Toyota", "Corolla", nextYearPlusTwo)
        );
    }
}
