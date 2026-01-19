package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.application.dto.UpdateVehicleDto;
import br.com.pitflow.registry.application.usecases.UpdateVehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

import java.util.UUID;

public class UpdateVehicleImp implements UpdateVehicle {
    private final VehicleRepository repository;

    public UpdateVehicleImp(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UpdateVehicleDto dto) {
        var vehicle = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        var newPlate = new LicensePlate(dto.licensePlate());

        // Check if the new license plate is different from the current one
        if (!vehicle.getLicensePlate().equals(newPlate)) {
            repository.findByLicensePlate(newPlate).ifPresent(v -> {
                throw new IllegalStateException("License plate already in use by another vehicle");
            });
        }

        vehicle.setBrand(dto.brand());
        vehicle.setModel(dto.model());
        vehicle.setYear(dto.year());
        vehicle.setLicensePlate(newPlate);

        repository.save(vehicle);
    }
}