package br.com.pitflow.registry.application;

import br.com.pitflow.registry.application.usecases.ListCustomers;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;

import java.util.List;

public class ListCustomersImp implements ListCustomers {
    private final CustomerRepository repository;

    public ListCustomersImp(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Customer> execute() {
        return repository.findAll();
    }
}