package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.application.dto.CreateCustomerDto;
import br.com.pitflow.registry.application.usecases.CreateCustomer;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;

public class CreateCustomerImp implements CreateCustomer {
    private final CustomerRepository repository;

    public CreateCustomerImp(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer execute(CreateCustomerDto dto) {
        var document = new CpfCnpj(dto.document());

        repository.findByDocument(document).ifPresent(customer -> {
            var message = String.format("Customer with document %s already exists.", document.value());
            throw new IllegalStateException(message);
        });

        var customer = new Customer(dto.name(), document, dto.phone());
        repository.save(customer);
        return customer;
    }
}
