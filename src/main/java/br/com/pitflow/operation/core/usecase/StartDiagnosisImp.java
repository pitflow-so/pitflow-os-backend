package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.StartDiagnosis;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class StartDiagnosisImp implements StartDiagnosis {

    private final ServiceOrderGateway repository;

    public StartDiagnosisImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        // change OS status to IN_DIAGNOSIS
        os.startDiagnosis();

        repository.save(os);
    }
}