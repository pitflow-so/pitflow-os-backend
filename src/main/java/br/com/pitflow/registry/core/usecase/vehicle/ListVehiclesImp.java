package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.usecase.vehicle.inputPort.ListVehicles;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;

import java.util.List;

public class ListVehiclesImp implements ListVehicles {
    private final VehicleGateway gateway;

    public ListVehiclesImp(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public List<Vehicle> execute() {
        return gateway.findAll();
    }
}