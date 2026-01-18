package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.UpdateServiceDto;
import br.com.pitflow.inventory.application.usecase.UpdateService;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;

import java.util.UUID;

public class UpdateServiceImp implements UpdateService {
    private final ServiceRepository repository;

    public UpdateServiceImp(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UpdateServiceDto dto) {
        var service = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        service.setName(dto.name());
        service.setDescription(dto.description());
        service.setPrice(dto.price());

        repository.save(service);
    }
}