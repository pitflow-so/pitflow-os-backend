package br.com.pitflow.registry.core.usecase.customer.inputPort;

import br.com.pitflow.registry.core.entity.Customer;

public interface FindCustomerByDocument {
    Customer execute(String document);
}
