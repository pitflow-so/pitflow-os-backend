package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.FindPartById;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;

import java.util.UUID;

public class FindPartByIdImp implements FindPartById {
    private final PartRepository repository;
    public FindPartByIdImp(PartRepository repository) { this.repository = repository; }

    @Override
    public Part execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + id));
    }
}