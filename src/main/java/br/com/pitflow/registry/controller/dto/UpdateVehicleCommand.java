package br.com.pitflow.registry.controller.dto;

public record UpdateVehicleCommand(String brand, String model, int year, String licensePlate) {
}
