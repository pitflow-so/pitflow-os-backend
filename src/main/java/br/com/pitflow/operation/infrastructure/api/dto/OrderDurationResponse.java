package br.com.pitflow.operation.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Informações de duração de uma Ordem de Serviço específica")
public record OrderDurationResponse(
        @Schema(description = "ID da OS", example = "UUID")
        UUID serviceOrderId,

        @Schema(description = "Duração total em minutos", example = "120.0")
        Double durationInMinutes,

        @Schema(description = "Tempo formatado", example = "2h 0min")
        String formattedDuration,

        @Schema(description = "Indica se a OS ainda está em execução", example = "false")
        boolean isStillRunning
) {}
