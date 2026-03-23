package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.DeleteVehicle;

import java.util.UUID;

public class DeleteVehicleImp implements DeleteVehicle {
    private final VehicleGateway repository;

    public DeleteVehicleImp(VehicleGateway repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot delete: Vehicle not found with ID: " + id));

        repository.deleteById(id);
    }
}