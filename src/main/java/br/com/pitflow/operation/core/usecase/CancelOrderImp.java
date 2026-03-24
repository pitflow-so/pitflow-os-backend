package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.CancelOrderCommand;
import br.com.pitflow.operation.core.usecase.inputPort.CancelOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

public class CancelOrderImp implements CancelOrder {

    private final ServiceOrderGateway repository;

    public CancelOrderImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(CancelOrderCommand dto) {
        if (dto.reason() == null || dto.reason().isBlank()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }

        var os = repository.findById(dto.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + dto.serviceOrderId()));

        // Change status to CANCELLED
        os.cancel(dto.reason());

        repository.save(os);
    }
}