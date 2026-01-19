package br.com.pitflow.operation.application.usecase;

import br.com.pitflow.operation.infrastructure.api.dto.ExecutionTimeMetricsResponse;

public interface GetAverageExecutionTime {
    ExecutionTimeMetricsResponse execute();
}
