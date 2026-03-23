package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.DeliverOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class DeliverOrderImp implements DeliverOrder {

    private final ServiceOrderGateway repository;

    public DeliverOrderImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        // Change status to DELIVERED
        os.deliver();

        repository.save(os);
    }
}