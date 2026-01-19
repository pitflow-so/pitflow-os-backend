package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.application.dto.AddVehicleDto;
import br.com.pitflow.registry.domain.Vehicle;

public interface AddVehicle {
    Vehicle execute(AddVehicleDto dto);
}
