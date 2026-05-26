package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import br.com.pitflow.operation.core.usecase.inputPort.StartDiagnosis;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class StartDiagnosisImp implements StartDiagnosis {

    private final ServiceOrderGateway repository;
    private final ServiceOrderMetricsGateway metricsGateway;

    public StartDiagnosisImp(ServiceOrderGateway repository,
                             ServiceOrderMetricsGateway metricsGateway
    ) {
        this.repository = repository;
        this.metricsGateway = metricsGateway;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        String previousStatus = os.getStatus().name();
        Duration timeSpentToInitiateDiagnosis = Duration.between(os.getCreatedAt(), LocalDateTime.now());

        // change OS status to IN_DIAGNOSIS
        os.startDiagnosis();

        repository.save(os);

        metricsGateway.recordTimeInStatus(previousStatus, timeSpentToInitiateDiagnosis);
    }
}