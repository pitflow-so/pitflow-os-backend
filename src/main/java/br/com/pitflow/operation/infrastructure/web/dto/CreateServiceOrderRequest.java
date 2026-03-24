package br.com.pitflow.operation.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;


@Schema(description = "Dados necessários para abrir uma nova Ordem de Serviço")
public record CreateServiceOrderRequest(
        @Schema(description = "ID único do cliente (UUID)", example = "UUID")
        UUID customerId,

        @Schema(description = "ID único do veículo (UUID)", example = "UUID")
        UUID vehicleId,

        @Schema(description = "Cliste informa o problema que está tendo", example = "UUID")
        String description
) {}