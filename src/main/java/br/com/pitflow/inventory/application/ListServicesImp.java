package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.ListServices;
import br.com.pitflow.inventory.domain.Service;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;

import java.util.List;

public class ListServicesImp implements ListServices {
    private final ServiceRepository repository;

    public ListServicesImp(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Service> execute() {
        return repository.findAll();
    }
}
