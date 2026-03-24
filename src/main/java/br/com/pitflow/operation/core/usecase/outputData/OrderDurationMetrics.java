package br.com.pitflow.operation.core.usecase.outputData;

import java.util.UUID;

public record OrderDurationMetrics(
        UUID serviceOrderId,
        Double durationInMinutes,
        String formattedDuration,
        boolean isStillRunning
) {
}
