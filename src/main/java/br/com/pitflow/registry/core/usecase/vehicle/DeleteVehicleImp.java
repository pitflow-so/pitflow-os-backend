package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.DeleteVehicle;

import java.util.UUID;

public class DeleteVehicleImp implements DeleteVehicle {
    private final VehicleGateway gateway;

    public DeleteVehicleImp(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UUID id) {
        gateway.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot delete: Vehicle not found with ID: " + id));

        gateway.deleteById(id);
    }
}