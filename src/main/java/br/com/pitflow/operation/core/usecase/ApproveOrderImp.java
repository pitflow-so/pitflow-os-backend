package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.ApproveOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class ApproveOrderImp implements ApproveOrder {

    private final ServiceOrderGateway repository;

    public ApproveOrderImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        // Change status to IN_EXECUTION
        os.approve();

        repository.save(os);
    }
}