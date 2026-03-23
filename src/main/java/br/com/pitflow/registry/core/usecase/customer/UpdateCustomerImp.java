package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.controller.dto.UpdateCustomerCommand;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;

import java.util.UUID;

public class UpdateCustomerImp implements UpdateCustomer {
    private final CustomerGateway repository;

    public UpdateCustomerImp(CustomerGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UpdateCustomerCommand dto) {
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