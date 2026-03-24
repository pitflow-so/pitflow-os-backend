package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleById;
import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.UUID;

public class FindVehicleByIdImp implements FindVehicleById {
    private final VehicleGateway gateway;

    public FindVehicleByIdImp(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Vehicle execute(UUID id) {
        return gateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
    }
}