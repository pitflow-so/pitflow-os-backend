package br.com.pitflow.registry.core.usecase.vehicle;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.controller.dto.AddVehicleCommand;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.AddVehicle;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.CustomerGateway;

public class AddVehicleImp implements AddVehicle {

    private final VehicleGateway vehicleGateway;
    private final CustomerGateway customerGateway;

    public AddVehicleImp(VehicleGateway vehicleGateway, CustomerGateway customerGateway) {
        this.vehicleGateway = vehicleGateway;
        this.customerGateway = customerGateway;
    }

    @Override
    public Vehicle execute(AddVehicleCommand dto) {
        customerGateway.findById(dto.customerId())
                .orElseThrow(
                () -> new IllegalArgumentException("Customer not found with ID: " + dto.customerId())
        );

        var licensePlate = new LicensePlate(dto.licensePlate());
        vehicleGateway.findByLicensePlate(licensePlate).ifPresent(v -> {
            throw new IllegalStateException("Vehicle with license plate " + licensePlate.value() + " already exists.");
        });

        var vehicle = new Vehicle(
                dto.customerId(),
                licensePlate,
                dto.brand(),
                dto.model(),
                dto.year()
        );

        vehicleGateway.save(vehicle);
        return vehicle;
    }
}