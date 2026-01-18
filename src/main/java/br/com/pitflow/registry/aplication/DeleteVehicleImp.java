package br.com.pitflow.registry.aplication;

import br.com.pitflow.registry.aplication.usecases.DeleteVehicle;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

import java.util.UUID;

public class DeleteVehicleImp implements DeleteVehicle {
    private final VehicleRepository repository;

    public DeleteVehicleImp(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id) {
        repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot delete: Vehicle not found with ID: " + id));

        repository.deleteById(id);
    }
}