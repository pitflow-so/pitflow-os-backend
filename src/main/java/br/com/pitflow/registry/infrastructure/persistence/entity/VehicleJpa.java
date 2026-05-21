package br.com.pitflow.registry.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "vehicle")
public class VehicleJpa {
    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(name = "model_year", nullable = false)
    private int year;

    public VehicleJpa() {}

    public VehicleJpa(UUID id, UUID customerId, String value, String brand, String model, int year) {
        this.id = id;
        this.customerId = customerId;
        this.licensePlate = value;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public UUID getId() {
        return id;
    }
}
