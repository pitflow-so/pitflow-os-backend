package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.application.dto.CreateCustomerDto;
import br.com.pitflow.registry.domain.Customer;

public interface CreateCustomer {
    Customer execute(CreateCustomerDto dto);
}
