package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.usecase.customer.inputPort.ListCustomers;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;

import java.util.List;

public class ListCustomersImp implements ListCustomers {
    private final CustomerGateway repository;

    public ListCustomersImp(CustomerGateway repository) {
        this.repository = repository;
    }

    @Override
    public List<Customer> execute() {
        return repository.findAll();
    }
}