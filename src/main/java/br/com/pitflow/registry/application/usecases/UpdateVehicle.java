package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.aplication.dto.UpdateVehicleDto;

import java.util.UUID;

public interface UpdateVehicle {
    void execute(UUID id, UpdateVehicleDto dto);
}
