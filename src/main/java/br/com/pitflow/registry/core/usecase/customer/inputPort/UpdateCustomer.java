package br.com.pitflow.registry.core.usecase.customer.inputPort;

import br.com.pitflow.registry.controller.dto.UpdateCustomerCommand;

import java.util.UUID;

public interface UpdateCustomer {
    void  execute(UUID id, UpdateCustomerCommand dto);
}
