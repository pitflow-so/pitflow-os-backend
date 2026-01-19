package br.com.pitflow.registry.application.usecases;

import java.util.UUID;

public interface DeleteVehicle {
    void execute(UUID id);
}
