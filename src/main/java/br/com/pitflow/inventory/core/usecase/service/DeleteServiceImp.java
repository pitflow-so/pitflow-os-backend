package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.core.usecase.service.inputPort.DeleteService;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;

import java.util.UUID;

public class DeleteServiceImp implements DeleteService {
    private final ServiceGateway repository;

    public DeleteServiceImp(ServiceGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        repository.deleteById(id);
    }
}