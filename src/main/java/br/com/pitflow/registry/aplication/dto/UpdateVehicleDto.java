package br.com.pitflow.registry.aplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualização de um veículo existente")
public record UpdateVehicleDto(
        @Schema(description = "Marca do veículo", example = "Toyota")
        String brand,

        @Schema(description = "Modelo do veículo", example = "Corolla")
        String model,

        @Schema(description = "Ano de fabricação", example = "2022")
        int year,

        @Schema(description = "Placa do veículo", example = "ABC1D23")
        String licensePlate
) {}