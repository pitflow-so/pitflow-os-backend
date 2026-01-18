package br.com.pitflow.registry.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados de retorno do veículo cadastrado")
public record VehicleResponse(
        @Schema(example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID id,

        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        UUID customerId,

        @Schema(example = "ABC-1234")
        String licensePlate,

        @Schema(example = "Toyota")
        String brand,

        @Schema(example = "Corolla")
        String model,

        @Schema(example = "2024")
        int year
) {}