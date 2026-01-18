package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.CreateServiceDto;
import br.com.pitflow.inventory.application.usecase.CreateService;
import br.com.pitflow.inventory.domain.Service;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;

public class CreateServiceImp implements CreateService {

    private final ServiceRepository serviceRepository;

    public CreateServiceImp(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Service execute(CreateServiceDto dto) {
        var service = new Service(
                dto.name(),
                dto.description(),
                dto.price()
        );

        serviceRepository.save(service);
        return service;
    }
}