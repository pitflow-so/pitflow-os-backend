package br.com.pitflow.inventory.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Representação de um serviço cadastrado")
public record ServiceResponse(
        @Schema(description = "Identificador único do serviço", example = "a23e8400-e29b-41d4-a716-446655440111")
        UUID id,

        @Schema(description = "Nome do serviço", example = "Alinhamento e Balanceamento")
        String name,

        @Schema(description = "Descrição do serviço", example = "Serviço completo para as 4 rodas incluindo conferência de suspensão")
        String description,

        @Schema(description = "Preço do serviço", example = "150.00")
        BigDecimal price
) {}