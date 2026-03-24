package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.controller.dto.UpdateCustomerCommand;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;

import java.util.UUID;

public class UpdateCustomerImp implements UpdateCustomer {
    private final CustomerGateway gateway;

    public UpdateCustomerImp(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UUID id, UpdateCustomerCommand dto) {
        var customer = gateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        var newDocument = new CpfCnpj(dto.document());

        // Validate document uniqueness only if it has changed
        if (!customer.getDocument().equals(newDocument)) {
            gateway.findByDocument(newDocument).ifPresent(c -> {
                throw new IllegalStateException("Document already in use by another customer");
            });
        }

        customer.setName(dto.name());
        customer.setDocument(newDocument);
        customer.setPhone(dto.phone());

        gateway.save(customer);
    }
}