package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.FindServiceById;
import br.com.pitflow.inventory.domain.Service;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;

import java.util.UUID;

public class FindServiceByIdImp implements FindServiceById {
    private final ServiceRepository repository;
    public FindServiceByIdImp(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Service execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }
}
