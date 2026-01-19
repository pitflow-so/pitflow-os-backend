package br.com.pitflow.registry.application;

import br.com.pitflow.registry.application.usecases.FindVehicleById;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

import java.util.UUID;

public class FindVehicleByIdImp implements FindVehicleById {
    private final VehicleRepository repository;

    public FindVehicleByIdImp(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
    }
}