package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.domain.Customer;

public interface FindCustomerByDocument {
    Customer execute(String document);
}
