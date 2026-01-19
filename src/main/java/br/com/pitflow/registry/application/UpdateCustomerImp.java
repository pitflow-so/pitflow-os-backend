package br.com.pitflow.registry.aplication;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.aplication.dto.UpdateCustomerDto;
import br.com.pitflow.registry.aplication.usecases.UpdateCustomer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;

import java.util.UUID;

public class UpdateCustomerImp implements UpdateCustomer {
    private final CustomerRepository repository;

    public UpdateCustomerImp(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UpdateCustomerDto dto) {
        var customer = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        var newDocument = new CpfCnpj(dto.document());

        // Validate document uniqueness only if it has changed
        if (!customer.getDocument().equals(newDocument)) {
            repository.findByDocument(newDocument).ifPresent(c -> {
                throw new IllegalStateException("Document already in use by another customer");
            });
        }

        customer.setName(dto.name());
        customer.setDocument(newDocument);
        customer.setPhone(dto.phone());

        repository.save(customer);
    }
}