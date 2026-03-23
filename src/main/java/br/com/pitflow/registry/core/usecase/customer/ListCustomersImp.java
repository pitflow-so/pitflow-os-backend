package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.usecase.customer.inputPort.ListCustomers;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;

import java.util.List;

public class ListCustomersImp implements ListCustomers {
    private final CustomerGateway gateway;

    public ListCustomersImp(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<Customer> execute() {
        return gateway.findAll();
    }
}