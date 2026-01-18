package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.domain.Vehicle;

public interface FindVehicleByPlate {
    Vehicle execute(String plate);
}
