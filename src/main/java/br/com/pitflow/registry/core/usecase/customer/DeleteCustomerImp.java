package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.DeleteCustomer;

import java.util.UUID;

public class DeleteCustomerImp implements DeleteCustomer {
    private final CustomerGateway gateway;

    public DeleteCustomerImp(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UUID id) {
        gateway.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        gateway.delete(id);
    }
}