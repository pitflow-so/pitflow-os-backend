package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.FindPartById;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.UUID;

public class FindPartByIdImp implements FindPartById {
    private final PartGateway repository;
    public FindPartByIdImp(PartGateway repository) { this.repository = repository; }

    @Override
    public Part execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + id));
    }
}