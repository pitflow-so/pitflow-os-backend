package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.application.dto.UpdateVehicleDto;

import java.util.UUID;

public interface UpdateVehicle {
    void execute(UUID id, UpdateVehicleDto dto);
}
