package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import br.com.pitflow.registry.controller.dto.UpdateVehicleCommand;

import java.util.UUID;

public interface UpdateVehicle {
    void execute(UUID id, UpdateVehicleCommand dto);
}
