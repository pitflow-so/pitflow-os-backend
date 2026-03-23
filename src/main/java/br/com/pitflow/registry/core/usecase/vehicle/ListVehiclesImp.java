package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.usecase.vehicle.inputPort.ListVehicles;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;

import java.util.List;

public class ListVehiclesImp implements ListVehicles {
    private final VehicleGateway repository;

    public ListVehiclesImp(VehicleGateway repository) {
        this.repository = repository;
    }

    @Override
    public List<Vehicle> execute() {
        return repository.findAll();
    }
}