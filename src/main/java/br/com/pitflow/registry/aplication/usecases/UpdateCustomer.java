package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.aplication.dto.UpdateCustomerDto;

import java.util.UUID;

public interface UpdateCustomer {
    void  execute(UUID id, UpdateCustomerDto dto);
}
