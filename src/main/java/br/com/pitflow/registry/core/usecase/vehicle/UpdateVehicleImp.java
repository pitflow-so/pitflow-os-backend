package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.controller.dto.UpdateVehicleCommand;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.UpdateVehicle;

import java.util.UUID;

public class UpdateVehicleImp implements UpdateVehicle {
    private final VehicleGateway gateway;

    public UpdateVehicleImp(VehicleGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UUID id, UpdateVehicleCommand dto) {
        var vehicle = gateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        var newPlate = new LicensePlate(dto.licensePlate());

        // Check if the new license plate is different from the current one
        if (!vehicle.getLicensePlate().equals(newPlate)) {
            gateway.findByLicensePlate(newPlate).ifPresent(v -> {
                throw new IllegalStateException("License plate already in use by another vehicle");
            });
        }

        vehicle.setBrand(dto.brand());
        vehicle.setModel(dto.model());
        vehicle.setYear(dto.year());
        vehicle.setLicensePlate(newPlate);

        gateway.save(vehicle);
    }
}