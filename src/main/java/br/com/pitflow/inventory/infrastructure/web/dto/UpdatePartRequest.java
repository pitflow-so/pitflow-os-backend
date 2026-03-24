package br.com.pitflow.inventory.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados para atualização de uma peça existente")
public record UpdatePartRequest(
        @Schema(description = "Novo SKU da peça", example = "FIL-12345-ALT")
        String sku,

        @Schema(description = "Novo nome da peça", example = "Filtro de Óleo Premium")
        String name,

        @Schema(description = "Nova descrição", example = "Filtro de alta eficiência para motores turbo")
        String description,

        @Schema(description = "Novo preço de venda", example = "55.00")
        BigDecimal price,

        @Schema(description = "Nova quantidade em estoque", example = "25")
        int stockQuantity
) {}