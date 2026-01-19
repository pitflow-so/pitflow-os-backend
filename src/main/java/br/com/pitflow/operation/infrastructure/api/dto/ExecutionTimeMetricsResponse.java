package br.com.pitflow.operation.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Métricas de tempo de execução da oficina")
public record ExecutionTimeMetricsResponse(
        @Schema(description = "Tempo médio em minutos", example = "125.5")
        Double averageTimeInMinutes,

        @Schema(description = "Tempo médio formatado para leitura humana", example = "2h 5min")
        String formattedTime
) {}