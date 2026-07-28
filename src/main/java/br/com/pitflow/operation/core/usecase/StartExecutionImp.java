package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.StartExecution;

import java.util.UUID;

public class StartExecutionImp implements StartExecution {

    private final ServiceOrderGateway repository;

    public StartExecutionImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID orderId) {
        var serviceOrder = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service Order not found with ID: " + orderId));

        serviceOrder.startExecution();
        repository.save(serviceOrder);
    }
}
