package br.com.pitflow.operation.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Detalhes de um item (peça ou serviço) contido na Ordem de Serviço")
public record ServiceOrderItemResponse(
        @Schema(description = "ID do item no catálogo de inventário", example = "8e9f0a1b-2c3d-4e5f-6a7b-8c9d0e1f2a3b")
        UUID catalogId,

        @Schema(description = "Nome ou descrição técnica do item", example = "Amortecedor Dianteiro Cofap")
        String description,

        @Schema(description = "Preço unitário no momento da adição", example = "200.00")
        BigDecimal unitPrice,

        @Schema(description = "Quantidade utilizada", example = "2")
        int quantity,

        @Schema(description = "Valor total do item (unitário * quantidade)", example = "400.00")
        BigDecimal totalPrice
) {}