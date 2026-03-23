package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleByPlate;
import br.com.pitflow.registry.core.entity.Vehicle;

public class FindVehicleByPlateImp implements FindVehicleByPlate {
    private final VehicleGateway repository;

    public FindVehicleByPlateImp(VehicleGateway repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle execute(String plate) {
        var licensePlate = new LicensePlate(plate);
        return repository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with plate: " + plate));
    }
}