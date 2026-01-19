package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.application.usecases.FindVehicleByPlate;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

public class FindVehicleByPlateImp implements FindVehicleByPlate {
    private final VehicleRepository repository;

    public FindVehicleByPlateImp(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle execute(String plate) {
        var licensePlate = new LicensePlate(plate);
        return repository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with plate: " + plate));
    }
}