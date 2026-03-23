package br.com.pitflow.registry.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
@Schema(description = "Dados para adicionar veículo")
public record AddVehicleRequest(
        @Schema(description = "Identificador do cliente", example = "UUID")
        UUID customerId,

        @Schema(description = "Placa do veículo", example = "ABC1234")
        String licensePlate,

        @Schema(description = "Marca do veículo", example = "Toyota")
        String brand,

        @Schema(description = "Modelo do veículo", example = "Corolla")
        String model,

        @Schema(description = "Ano do veículo", example = "2024")
        int year
) {}