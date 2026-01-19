package br.com.pitflow.operation.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados para cancelamento de uma Ordem de Serviço")
public record CancelOrderDto(
        @Schema(description = "ID da Ordem de Serviço", example = "UUID")
        UUID serviceOrderId,

        @Schema(description = "Necessário informar o motivo do cancelamento", example = "Cliente desistiu do serviço")
        String reason
) {}