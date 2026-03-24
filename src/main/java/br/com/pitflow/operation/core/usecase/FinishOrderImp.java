package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.FinishOrder;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class FinishOrderImp implements FinishOrder {
    private final NotificationGateway notificationGateway;
    private final ServiceOrderGateway repository;

    public FinishOrderImp(NotificationGateway notificationGateway, ServiceOrderGateway repository) {
        this.notificationGateway = notificationGateway;
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
        notificationGateway.send(os.getId(), message);
    }
}