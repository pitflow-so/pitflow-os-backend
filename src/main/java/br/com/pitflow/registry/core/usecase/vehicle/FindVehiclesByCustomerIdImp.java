package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehiclesByCustomerId;
import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.List;
import java.util.UUID;

public class FindVehiclesByCustomerIdImp implements FindVehiclesByCustomerId {
    private final VehicleGateway gateway;

    public FindVehiclesByCustomerIdImp(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<Vehicle> execute(UUID customerId) {
        return gateway.findByCustomerId(customerId);
    }
}