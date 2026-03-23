package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.DeleteCustomer;

import java.util.UUID;

public class DeleteCustomerImp implements DeleteCustomer {
    private final CustomerGateway repository;

    public DeleteCustomerImp(CustomerGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        repository.delete(id);
    }
}