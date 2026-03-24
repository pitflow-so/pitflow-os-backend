package br.com.pitflow.registry.core.usecase.customer.inputPort;

import br.com.pitflow.registry.core.entity.Customer;

import java.util.List;

public interface ListCustomers {
    List<Customer> execute();
}
