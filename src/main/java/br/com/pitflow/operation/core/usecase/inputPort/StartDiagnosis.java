package br.com.pitflow.operation.core.usecase.inputPort;

import java.util.UUID;

public interface StartDiagnosis {
    void execute(UUID orderId);
}
