package br.com.pitflow.operation.application.dto;

import br.com.pitflow.operation.domain.ServiceOrder.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Dados para inclusão de peça ou serviço na Ordem de Serviço")
public record AddOrderItemDto(
        @Schema(description = "ID da Ordem de Serviço", example = "UUID")
        UUID serviceOrderId,

        @Schema(description = "ID do item no catálogo (Peça ou Serviço)", example = "UUID")
        UUID catalogId,

        @Schema(description = "Quantidade (para serviços, geralmente 1)", example = "1")
        int quantity,

        @Schema(description = "Tipo do item: PART ou SERVICE", example = "PART")
        ItemType type
) {}
