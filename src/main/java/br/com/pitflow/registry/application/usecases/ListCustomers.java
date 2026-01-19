package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.domain.Customer;

import java.util.List;

public interface ListCustomers {
    List<Customer> execute();
}
