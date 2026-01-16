package br.com.pitflow.registry.domain;

import br.com.pitflow.common.valueobject.LicensePlate;

import java.time.LocalDate;
import java.util.UUID;

public class Vehicle {
    private UUID id;
    private LicensePlate licensePlate; // Value Object for plate validation
    private String brand;
    private String model;
    private int year;

    public Vehicle(LicensePlate licensePlate, String brand, String model, int year) {
        validateBrand(brand);
        validateModel(model);
        validateYear(year);

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

        // Basic rule: No future cars (allowing 1 year ahead for models) and no extremely old cars for MVP
        if (year < 1900 || year > currentYear + 1) {
            throw new IllegalArgumentException("Invalid vehicle year: " + year);
        }
    }

    public UUID getId() { return id; }
    public LicensePlate getLicensePlate() { return licensePlate; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }

    public void setId(UUID id) { this.id = id; }
}
