package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleById;
import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.UUID;

public class FindVehicleByIdImp implements FindVehicleById {
    private final VehicleGateway repository;

    public FindVehicleByIdImp(VehicleGateway repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
    }
}