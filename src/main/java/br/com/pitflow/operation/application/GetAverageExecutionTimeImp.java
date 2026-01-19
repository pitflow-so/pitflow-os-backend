package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.GetAverageExecutionTime;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.operation.infrastructure.api.dto.ExecutionTimeMetricsResponse;

public class GetAverageExecutionTimeImp implements GetAverageExecutionTime {
    private final ServiceOrderRepository repository;

    public GetAverageExecutionTimeImp(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public ExecutionTimeMetricsResponse execute() {
        Double avgSeconds = repository.getAverageExecutionTimeInSeconds();

        if (avgSeconds == null || avgSeconds == 0) {
            return new ExecutionTimeMetricsResponse(0.0, "0min");
        }

        double minutes = avgSeconds / 60;
        long hours = (long) (minutes / 60);
        long remainingMinutes = (long) (minutes % 60);

        String formatted = hours > 0
                ? String.format("%dh %dmin", hours, remainingMinutes)
                : String.format("%dmin", remainingMinutes);

        return new ExecutionTimeMetricsResponse(minutes, formatted);
    }
}