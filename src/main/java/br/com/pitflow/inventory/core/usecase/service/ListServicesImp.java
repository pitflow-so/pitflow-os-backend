package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.core.usecase.service.inputPort.ListServices;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;

import java.util.List;

public class ListServicesImp implements ListServices {
    private final ServiceGateway repository;

    public ListServicesImp(ServiceGateway repository) {
        this.repository = repository;
    }

    @Override
    public List<Service> execute() {
        return repository.findAll();
    }
}
