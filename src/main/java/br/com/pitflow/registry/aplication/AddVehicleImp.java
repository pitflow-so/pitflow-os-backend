package br.com.pitflow.registry.aplication;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.aplication.dto.AddVehicleDto;
import br.com.pitflow.registry.aplication.usecases.AddVehicle;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

public class AddVehicleImp implements AddVehicle {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    public AddVehicleImp(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Vehicle execute(AddVehicleDto dto) {
        customerRepository.findById(dto.customerId())
                .orElseThrow(
                () -> new IllegalArgumentException("Customer not found with ID: " + dto.customerId())
        );

        var licensePlate = new LicensePlate(dto.licensePlate());
        vehicleRepository.findByLicensePlate(licensePlate).ifPresent(v -> {
            throw new IllegalStateException("Vehicle with license plate " + licensePlate.value() + " already exists.");
        });

        var vehicle = new Vehicle(
                dto.customerId(),
                licensePlate,
                dto.brand(),
                dto.model(),
                dto.year()
        );

        vehicleRepository.save(vehicle);
        return vehicle;
    }
}