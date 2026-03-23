package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.core.usecase.outputData.ExecutionTimeMetrics;

public interface GetAverageExecutionTime {
    ExecutionTimeMetrics execute();
}
