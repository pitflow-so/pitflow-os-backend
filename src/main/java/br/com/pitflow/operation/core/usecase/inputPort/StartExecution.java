package br.com.pitflow.operation.core.usecase.inputPort;

import java.util.UUID;

public interface StartExecution {
    void execute(UUID orderId);
}
