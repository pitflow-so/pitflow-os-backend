package br.com.pitflow.operation.application.usecase;

import java.util.UUID;

public interface CompleteDiagnosis {
    void execute(UUID orderId);
}
