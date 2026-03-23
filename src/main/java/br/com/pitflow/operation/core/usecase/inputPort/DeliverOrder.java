package br.com.pitflow.operation.core.usecase.inputPort;

import java.util.UUID;

public interface DeliverOrder {
    void execute(UUID orderId);
}
