package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.aplication.dto.CreateCustomerDto;
import br.com.pitflow.registry.domain.Customer;

public interface CreateCustomer {
    Customer execute(CreateCustomerDto dto);
}
