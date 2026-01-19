package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.DeliverOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.UUID;

public class DeliverOrderImp implements DeliverOrder {

    private final ServiceOrderRepository repository;

    public DeliverOrderImp(ServiceOrderRepository repository) {
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