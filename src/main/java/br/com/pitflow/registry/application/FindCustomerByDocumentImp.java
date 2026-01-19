package br.com.pitflow.registry.aplication;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.aplication.usecases.FindCustomerByDocument;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;

public class FindCustomerByDocumentImp implements FindCustomerByDocument {
    private final CustomerRepository repository;

    public FindCustomerByDocumentImp(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer execute(String document) {
        var cpfCnpj = new CpfCnpj(document);
        return repository.findByDocument(cpfCnpj)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with document: " + document));
    }
}