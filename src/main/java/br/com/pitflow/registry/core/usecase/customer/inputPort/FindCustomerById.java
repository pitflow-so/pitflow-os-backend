package br.com.pitflow.registry.core.usecase.customer.inputPort;

import br.com.pitflow.registry.core.entity.Customer;

import java.util.UUID;

public interface FindCustomerById {
    Customer execute(UUID id);
}
