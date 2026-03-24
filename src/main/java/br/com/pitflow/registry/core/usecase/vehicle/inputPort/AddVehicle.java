package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import br.com.pitflow.registry.controller.dto.AddVehicleCommand;
import br.com.pitflow.registry.core.entity.Vehicle;

public interface AddVehicle {
    Vehicle execute(AddVehicleCommand dto);
}
