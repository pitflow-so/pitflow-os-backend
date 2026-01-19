package br.com.pitflow.registry.application;

import br.com.pitflow.registry.application.usecases.ListVehicles;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

import java.util.List;

public class ListVehiclesImp implements ListVehicles {
    private final VehicleRepository repository;

    public ListVehiclesImp(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Vehicle> execute() {
        return repository.findAll();
    }
}