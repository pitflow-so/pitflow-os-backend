package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.DeletePart;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.UUID;

public class DeletePartImp implements DeletePart {
    private final PartGateway repository;

    public DeletePartImp(PartGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot delete: Part not found with ID: " + id));

        repository.deleteById(id);
    }
}