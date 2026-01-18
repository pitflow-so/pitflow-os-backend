package br.com.pitflow.registry.aplication;

import br.com.pitflow.registry.aplication.usecases.DeleteCustomer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;

import java.util.UUID;

public class DeleteCustomerImp implements DeleteCustomer {
    private final CustomerRepository repository;

    public DeleteCustomerImp(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        repository.deleteById(id);
    }
}