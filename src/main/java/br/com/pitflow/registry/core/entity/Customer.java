package br.com.pitflow.registry.core.entity;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.valueObject.Email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;
    private String phone;

    private Email email;
    private CpfCnpj document;
    private List<Vehicle> vehicles;

    public Customer(String name, String phone, Email email, CpfCnpj document) {
        validateName(name);
        this.id = UUID.randomUUID();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.document = document;
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
    public Email getEmail() { return email; }

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
