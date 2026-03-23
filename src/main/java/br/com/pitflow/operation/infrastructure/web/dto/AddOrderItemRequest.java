package br.com.pitflow.operation.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados para inclusão de peça ou serviço na Ordem de Serviço")
public record AddOrderItemRequest(
        @Schema(description = "ID da Ordem de Serviço", example = "UUID")
        UUID serviceOrderId,

        @Schema(description = "ID do item no catálogo (Peça ou Serviço)", example = "UUID")
        UUID catalogId,

        @Schema(description = "Quantidade (para serviços, geralmente 1)", example = "1")
        int quantity,

        @Schema(description = "Tipo do item: PART ou SERVICE", example = "PART")
        String type
) {}
