package br.com.pitflow.registry.controller.dto;

public record CreateCustomerCommand(String name, String document, String phone, String email) {
}
