package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.GetAverageExecutionTime;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.outputData.ExecutionTimeMetrics;

public class GetAverageExecutionTimeImp implements GetAverageExecutionTime {
    private final ServiceOrderGateway gateway;

    public GetAverageExecutionTimeImp(ServiceOrderGateway repository) {
        this.gateway = repository;
    }

    @Override
    public ExecutionTimeMetrics execute() {
        Double avgSeconds = gateway.getAverageExecutionTimeInSeconds();

        if (avgSeconds == null || avgSeconds == 0) {
            return new ExecutionTimeMetrics(0.0, "0min");
        }

        double minutes = avgSeconds / 60;
        long hours = (long) (minutes / 60);
        long remainingMinutes = (long) (minutes % 60);

        String formatted = hours > 0
                ? String.format("%dh %dmin", hours, remainingMinutes)
                : String.format("%dmin", remainingMinutes);

        return new ExecutionTimeMetrics(minutes, formatted);
    }
}