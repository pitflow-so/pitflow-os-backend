package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.GetServiceOrderDuration;
import br.com.pitflow.operation.core.usecase.outputData.OrderDurationMetrics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class GetServiceOrderDurationImp implements GetServiceOrderDuration {

    private final ServiceOrderGateway repository;

    public GetServiceOrderDurationImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public OrderDurationMetrics execute(UUID serviceOrderId) {
        var os = repository.findById(serviceOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found"));

        if (os.getExecutionStartedAt() == null) {
            return new OrderDurationMetrics(serviceOrderId, 0.0, "Não iniciada", false);
        }

        // If the order is not finished, calculate duration until now
        LocalDateTime endTime = (os.getFinishedAt() != null) ? os.getFinishedAt() : LocalDateTime.now();
        Duration duration = Duration.between(os.getExecutionStartedAt(), endTime);

        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        String formatted = hours > 0
                ? String.format("%dh %dmin", hours, minutes)
                : String.format("%dmin", minutes);

        return new OrderDurationMetrics(
                serviceOrderId,
                (double) totalMinutes,
                formatted,
                os.getFinishedAt() == null
        );
    }
}