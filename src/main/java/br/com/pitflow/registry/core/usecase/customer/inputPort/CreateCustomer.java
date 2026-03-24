package br.com.pitflow.registry.core.usecase.customer.inputPort;

import br.com.pitflow.registry.controller.dto.CreateCustomerCommand;
import br.com.pitflow.registry.core.entity.Customer;

public interface CreateCustomer {
    Customer execute(CreateCustomerCommand dto);
}
