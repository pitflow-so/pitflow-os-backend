package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.domain.Customer;

import java.util.UUID;

public interface FindCustomerById {
    Customer execute(UUID id);
}
