package br.com.pitflow.registry.core.usecase.customer;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerByDocument;
import br.com.pitflow.registry.core.entity.Customer;

public class FindCustomerByDocumentImp implements FindCustomerByDocument {
    private final CustomerGateway gateway;

    public FindCustomerByDocumentImp(CustomerGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Customer execute(String document) {
        var cpfCnpj = new CpfCnpj(document);
        return gateway.findByDocument(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with document: " + document));
    }
}