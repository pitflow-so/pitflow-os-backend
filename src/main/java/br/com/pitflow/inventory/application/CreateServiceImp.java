package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.CreateServiceDto;
import br.com.pitflow.inventory.application.usecase.CreateService;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;

public class CreateServiceImp implements CreateService {

    private final ServiceGateway serviceGateway;

    public CreateServiceImp(ServiceGateway serviceGateway) {
        this.serviceGateway = serviceGateway;
    }

    @Override
    public Service execute(CreateServiceDto dto) {
        var service = new Service(
                dto.name(),
                dto.description(),
                dto.price()
        );

        serviceGateway.save(service);
        return service;
    }
}