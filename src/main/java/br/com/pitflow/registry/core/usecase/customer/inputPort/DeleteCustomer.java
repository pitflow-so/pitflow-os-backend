package br.com.pitflow.registry.core.usecase.customer.inputPort;

import java.util.UUID;

public interface DeleteCustomer {
    void execute(UUID id);
}
