package br.com.pitflow.inventory.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Representação de uma peça cadastrada")
public record PartResponse(
        @Schema(description = "Identificador único da peça", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "SKU da peça", example = "FIL-12345")
        String sku,

        @Schema(description = "Nome da peça", example = "Filtro de Óleo Sintético")
        String name,

        @Schema(description = "Descrição da peça", example = "Filtro de óleo para motores de alta performance")
        String description,

        @Schema(description = "Preço de venda atual", example = "45.90")
        BigDecimal price,

        @Schema(description = "Quantidade total em estoque", example = "50")
        int stockQuantity
) {}