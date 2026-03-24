package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import br.com.pitflow.registry.core.entity.Vehicle;

public interface FindVehicleByPlate {
    Vehicle execute(String plate);
}
