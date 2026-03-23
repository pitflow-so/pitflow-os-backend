package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.GetServiceOrderById;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class GetServiceOrderByIdImp implements GetServiceOrderById {

    private final ServiceOrderGateway repository;

    public GetServiceOrderByIdImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public ServiceOrder execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + id));
    }
}