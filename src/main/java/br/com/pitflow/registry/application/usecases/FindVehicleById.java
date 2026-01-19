package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.domain.Vehicle;

import java.util.UUID;

public interface FindVehicleById {
    Vehicle execute(UUID id);
}
