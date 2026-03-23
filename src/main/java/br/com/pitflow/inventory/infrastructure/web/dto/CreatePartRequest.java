package br.com.pitflow.inventory.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de uma nova peça no estoque")
public record CreatePartRequest(
        @Schema(description = "Código identificador único da peça (Unidade de Manutenção de Estoque)", example = "FIL-12345")
        String sku,

        @Schema(description = "Nome resumido da peça", example = "Filtro de Óleo Sintético")
        String name,

        @Schema(description = "Descrição detalhada da peça e suas aplicações", example = "Filtro de óleo para motores de alta performance")
        String description,

        @Schema(description = "Preço de venda da peça", example = "45.90")
        BigDecimal price,

        @Schema(description = "Quantidade inicial disponível em estoque", example = "50")
        int initialStock
) {}