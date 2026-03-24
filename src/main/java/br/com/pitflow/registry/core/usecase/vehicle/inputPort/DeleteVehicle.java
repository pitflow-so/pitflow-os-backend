package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import java.util.UUID;

public interface DeleteVehicle {
    void execute(UUID id);
}
