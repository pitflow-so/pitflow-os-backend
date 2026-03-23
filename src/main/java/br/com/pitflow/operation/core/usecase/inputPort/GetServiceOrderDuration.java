package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.core.usecase.outputData.OrderDurationMetrics;

import java.util.UUID;

public interface GetServiceOrderDuration {
    OrderDurationMetrics execute(UUID serviceOrderId);
}
