package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.application.dto.UpdateCustomerDto;

import java.util.UUID;

public interface UpdateCustomer {
    void  execute(UUID id, UpdateCustomerDto dto);
}
