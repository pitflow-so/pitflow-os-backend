package br.com.pitflow.registry.application;

import br.com.pitflow.registry.application.usecases.FindVehiclesByCustomerId;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

import java.util.List;
import java.util.UUID;

public class FindVehiclesByCustomerIdImp implements FindVehiclesByCustomerId {
    private final VehicleRepository repository;

    public FindVehiclesByCustomerIdImp(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Vehicle> execute(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }
}