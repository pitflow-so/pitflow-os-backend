package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.UUID;

public interface FindVehicleById {
    Vehicle execute(UUID id);
}
