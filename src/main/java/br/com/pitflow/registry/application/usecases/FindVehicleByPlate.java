package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.domain.Vehicle;

public interface FindVehicleByPlate {
    Vehicle execute(String plate);
}
