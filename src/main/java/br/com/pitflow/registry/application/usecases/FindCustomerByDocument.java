package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.domain.Customer;

public interface FindCustomerByDocument {
    Customer execute(String document);
}
