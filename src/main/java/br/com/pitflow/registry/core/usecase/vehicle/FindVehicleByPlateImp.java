package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleByPlate;
import br.com.pitflow.registry.core.entity.Vehicle;

public class FindVehicleByPlateImp implements FindVehicleByPlate {
    private final VehicleGateway gateway;

    public FindVehicleByPlateImp(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Vehicle execute(String plate) {
        var licensePlate = new LicensePlate(plate);
        return gateway.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with plate: " + plate));
    }
}