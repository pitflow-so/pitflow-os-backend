package br.com.pitflow.inventory.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de um novo serviço no catálogo")
public record CreateServiceRequest(
        @Schema(description = "Nome do serviço de mão de obra", example = "Alinhamento e Balanceamento")
        String name,

        @Schema(description = "Detalhamento do serviço realizado", example = "Serviço completo para as 4 rodas incluindo conferência de suspensão")
        String description,

        @Schema(description = "Valor fixo do serviço", example = "150.00")
        BigDecimal price
) {}
