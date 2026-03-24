package br.com.pitflow.registry.presenter.dto;

import java.util.UUID;

public record VehicleResponse(
        UUID id,

        UUID customerId,

        String licensePlate,

        String brand,

        String model,

        int year
) {}