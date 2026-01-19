package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.StartDiagnosis;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.UUID;

public class StartDiagnosisImp implements StartDiagnosis {

    private final ServiceOrderRepository repository;

    public StartDiagnosisImp(ServiceOrderRepository repository) {
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