package br.com.pitflow.registry.core.entity;

import java.util.UUID;

public class Mechanic {
    private UUID id;
    private String name;
    private String username;
    private String password;
    private String role;

    public Mechanic(String name, String username, String password) {
        validate(name, username, password);
        this.id = UUID.randomUUID();
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = "ROLE_MECHANIC";
    }

    private void validate(String name, String username, String password) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setId(UUID id) { this.id = id; }
}