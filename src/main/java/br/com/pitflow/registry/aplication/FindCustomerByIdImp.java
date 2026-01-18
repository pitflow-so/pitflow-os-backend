package br.com.pitflow.registry.aplication;

import br.com.pitflow.registry.aplication.usecases.FindCustomerById;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;

import java.util.UUID;

public class FindCustomerByIdImp implements FindCustomerById {
    private final CustomerRepository repository;

    public FindCustomerByIdImp(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
    }
}