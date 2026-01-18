package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.DeleteService;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;

import java.util.UUID;

public class DeleteServiceImp implements DeleteService {
    private final ServiceRepository repository;

    public DeleteServiceImp(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        repository.deleteById(id);
    }
}