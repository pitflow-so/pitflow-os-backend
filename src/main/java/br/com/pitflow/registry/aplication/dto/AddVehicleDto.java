package br.com.pitflow.registry.aplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
@Schema(description = "Dados para adicionar veículo")
public record AddVehicleDto(
        @Schema(example = "3521a4f1-fa29-4386-8105-f5ae83282410", description = "Identificador do cliente")
        UUID customerId,

        @Schema(example = "ABC1234", description = "Placa do veículo")
        String licensePlate,

        @Schema(example = "Toyota", description = "Marca do veículo")
        String brand,

        @Schema(example = "Corolla", description = "Modelo do veículo")
        String model,

        @Schema(example = "2024", description = "Ano do veículo")
        int year
) {}