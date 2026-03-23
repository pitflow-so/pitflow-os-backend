package br.com.pitflow.registry.controller.dto;

import java.util.UUID;

public record AddVehicleCommand(UUID customerId,

                                String licensePlate,

                                String brand,

                                String model,

                                int year) {
}
