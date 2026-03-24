package br.com.pitflow.registry.core.entity;

import br.com.pitflow.registry.core.valueObject.LicensePlate;

import java.time.LocalDate;
import java.util.UUID;

public class Vehicle {
    private UUID id;
    private UUID customerId;
    private LicensePlate licensePlate;
    private String brand;
    private String model;
    private int year;

    public Vehicle(UUID customerId, LicensePlate licensePlate, String brand, String model, int year) {
        validateBrand(brand);
        validateModel(model);
        validateYear(year);

        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    private void validateBrand(String brand) {
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Vehicle brand cannot be empty.");
        }
    }

    private void validateModel(String model) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Vehicle model cannot be empty.");
        }
    }

    private void validateYear(int year) {
        int currentYear = LocalDate.now().getYear();

        if (year < 1900 || year > currentYear + 1) {
            throw new IllegalArgumentException("Invalid vehicle year: " + year);
        }
    }

    public UUID getId() { return id; }
    public LicensePlate getLicensePlate() { return licensePlate; }
    public UUID getCustomerId() { return customerId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }

    public void setId(UUID id) { this.id = id; }

    public void setBrand(String brand) {
        validateBrand(brand);
        this.brand = brand;
    }

    public void setLicensePlate(LicensePlate newPlate) {
        this.licensePlate = newPlate;
    }

    public void setModel(String model) {
        validateModel(model);
        this.model = model;
    }

    public void setYear(int year) {
        validateYear(year);
        this.year = year;
    }
}
