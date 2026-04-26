package br.com.pitflow.operation.core.usecase.inputPort;

import java.util.UUID;

public interface CompleteDiagnosis {
    void execute(UUID orderId);
    String getApiURl(); //TODO: remove it
}
