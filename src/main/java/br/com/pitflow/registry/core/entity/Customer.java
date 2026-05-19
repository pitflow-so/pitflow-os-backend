package br.com.pitflow.registry.core.entity;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.valueObject.Email;

import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;
    private String phone;
    private String status;

    private Email email;
    private CpfCnpj document;

    public Customer(String name, String phone, Email email, CpfCnpj document) {
        validateName(name);
        this.id = UUID.randomUUID();
        this.name = name;
        this.phone = phone;
        this.status = "ACTIVE";
        this.email = email;
        this.document = document;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
    }


    public UUID getId() { return id; }
    public String getName() { return name; }
    public CpfCnpj getDocument() { return document; }
    public String getPhone() { return phone; }
    public Email getEmail() { return email; }

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

    public String geStatus() {
        return this.status;
    }

    public void setStatus(String status) { this.status = status; }

}
