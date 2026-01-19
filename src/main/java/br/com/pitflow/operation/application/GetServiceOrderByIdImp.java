package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.GetServiceOrderById;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.UUID;

public class GetServiceOrderByIdImp implements GetServiceOrderById {

    private final ServiceOrderRepository repository;

    public GetServiceOrderByIdImp(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceOrder execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + id));
    }
}