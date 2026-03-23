package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerByDocument;
import br.com.pitflow.registry.core.entity.Customer;

public class FindCustomerByDocumentImp implements FindCustomerByDocument {
    private final CustomerGateway repository;

    public FindCustomerByDocumentImp(CustomerGateway repository) {
        this.repository = repository;
    }

    @Override
    public Customer execute(String document) {
        var cpfCnpj = new CpfCnpj(document);
        return repository.findByDocument(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with document: " + document));
    }
}