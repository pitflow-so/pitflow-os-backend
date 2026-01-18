package br.com.pitflow.registry.domain;

import br.com.pitflow.common.valueobject.CpfCnpj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;
    private CpfCnpj document;
    private String phone;
    private List<Vehicle> vehicles;

    public Customer(String name, CpfCnpj document, String phone) {
        validateName(name);
        this.id = UUID.randomUUID();
        this.name = name;
        this.document = document;
        this.phone = phone;
        this.vehicles = new ArrayList<>();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public CpfCnpj getDocument() { return document; }
    public String getPhone() { return phone; }

    // Ensure immutability of the vehicles list
    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setDocument(CpfCnpj newDocument) {
        this.document = newDocument;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
