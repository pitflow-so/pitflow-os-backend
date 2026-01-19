package br.com.pitflow.operation.application.usecase;

import java.util.UUID;

public interface StartDiagnosis {
    void execute(UUID orderId);
}
