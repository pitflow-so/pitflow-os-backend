package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.CompleteDiagnosis;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class CompleteDiagnosisImp implements CompleteDiagnosis {

    private final ServiceOrderGateway repository;
    private final NotificationGateway notificationGateway;

    public CompleteDiagnosisImp(ServiceOrderGateway repository, NotificationGateway notificationGateway) {
        this.repository = repository;
        this.notificationGateway = notificationGateway;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        os.completeDiagnosis();
        repository.save(os);

        String message = String.format("Seu orçamento para a OS %s está pronto. Valor total: R$ %s. Por favor, acesse o app para autorizar.",
                os.getId(), os.getTotalAmount());

        notificationGateway.send(os.getId(), message);
    }
}