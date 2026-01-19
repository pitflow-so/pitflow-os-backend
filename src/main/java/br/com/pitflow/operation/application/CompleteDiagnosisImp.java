package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.CompleteDiagnosis;
import br.com.pitflow.operation.application.usecase.NotificationService;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.UUID;

public class CompleteDiagnosisImp implements CompleteDiagnosis {

    private final ServiceOrderRepository repository;
    private final NotificationService notificationService;

    public CompleteDiagnosisImp(ServiceOrderRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        os.completeDiagnosis();
        repository.save(os);

        String message = String.format("Seu orçamento para a OS %s está pronto. Valor total: R$ %s. Por favor, acesse o app para autorizar.",
                os.getId(), os.getTotalAmount());

        notificationService.send(os.getId(), message);
    }
}