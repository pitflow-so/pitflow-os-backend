package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.controller.dto.CreateCustomerCommand;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer;
import br.com.pitflow.registry.core.valueObject.Email;

public class CreateCustomerImp implements CreateCustomer {
    private final CustomerGateway gateway;

    public CreateCustomerImp(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Customer execute(CreateCustomerCommand dto) {
        var document = new CpfCnpj(dto.document());
        var email = new Email(dto.email());

        gateway.findByDocument(document).ifPresent(customer -> {
            var message = String.format("Customer with document %s already exists.", document.value());
            throw new IllegalStateException(message);
        });

        var customer = new Customer(dto.name(), dto.phone(), email, document);
        gateway.save(customer);
        return customer;
    }
}
