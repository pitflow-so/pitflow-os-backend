package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.controller.dto.CreateServiceCommand;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.inventory.core.usecase.service.inputPort.CreateService;

public class CreateServiceImp implements CreateService {

    private final ServiceGateway serviceGateway;

    public CreateServiceImp(ServiceGateway serviceGateway) {
        this.serviceGateway = serviceGateway;
    }

    @Override
    public Service execute(CreateServiceCommand dto) {
        var service = new Service(
                dto.name(),
                dto.description(),
                dto.price()
        );

        serviceGateway.save(service);
        return service;
    }
}