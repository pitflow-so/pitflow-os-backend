package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.controller.dto.UpdateServiceCommand;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.inventory.core.usecase.service.inputPort.UpdateService;

import java.util.UUID;

public class UpdateServiceImp implements UpdateService {
    private final ServiceGateway repository;

    public UpdateServiceImp(ServiceGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UpdateServiceCommand dto) {
        var service = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        service.setName(dto.name());
        service.setDescription(dto.description());
        service.setPrice(dto.price());

        repository.save(service);
    }
}