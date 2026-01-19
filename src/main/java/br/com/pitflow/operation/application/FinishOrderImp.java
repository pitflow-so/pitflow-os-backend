package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.FinishOrder;
import br.com.pitflow.operation.application.usecase.NotificationService;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.UUID;

public class FinishOrderImp implements FinishOrder {
    private final NotificationService notificationService;
    private final ServiceOrderRepository repository;

    public FinishOrderImp(NotificationService notificationService, ServiceOrderRepository repository) {
        this.notificationService = notificationService;
        this.repository = repository;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        // Change status to FINISHED
        os.finish();

        repository.save(os);

        String message = String.format("Sua Ordem de Serviço %s foi finalizada.",
                os.getId());
        notificationService.send(os.getId(), message);
    }
}