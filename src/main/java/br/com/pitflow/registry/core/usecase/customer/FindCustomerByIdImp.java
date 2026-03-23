package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerById;
import br.com.pitflow.registry.core.entity.Customer;

import java.util.UUID;

public class FindCustomerByIdImp implements FindCustomerById {
    private final CustomerGateway gateway;

    public FindCustomerByIdImp(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Customer execute(UUID id) {
        return gateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
    }
}