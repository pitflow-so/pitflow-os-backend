package br.com.pitflow.registry.controller.dto;

public record UpdateCustomerCommand(String name, String document, String phone, String email) {
}
