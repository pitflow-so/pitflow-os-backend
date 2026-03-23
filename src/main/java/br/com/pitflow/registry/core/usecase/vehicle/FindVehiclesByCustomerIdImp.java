package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehiclesByCustomerId;
import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.List;
import java.util.UUID;

public class FindVehiclesByCustomerIdImp implements FindVehiclesByCustomerId {
    private final VehicleGateway repository;

    public FindVehiclesByCustomerIdImp(VehicleGateway repository) {
        this.repository = repository;
    }

    @Override
    public List<Vehicle> execute(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }
}