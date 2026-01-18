package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.aplication.dto.AddVehicleDto;
import br.com.pitflow.registry.domain.Vehicle;

public interface AddVehicle {
    Vehicle execute(AddVehicleDto dto);
}
