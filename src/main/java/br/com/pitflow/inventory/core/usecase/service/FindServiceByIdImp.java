package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.core.usecase.service.inputPort.FindServiceById;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;

import java.util.UUID;

public class FindServiceByIdImp implements FindServiceById {
    private final ServiceGateway repository;
    public FindServiceByIdImp(ServiceGateway repository) {
        this.repository = repository;
    }

    @Override
    public Service execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
}
