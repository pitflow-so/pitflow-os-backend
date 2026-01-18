package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.DeletePart;
import br.com.pitflow.inventory.domain.repository.PartRepository;

import java.util.UUID;

public class DeletePartImp implements DeletePart {
    private final PartRepository repository;

    public DeletePartImp(PartRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot delete: Part not found with ID: " + id));

        repository.deleteById(id);
    }
}