package br.com.pitflow.registry.aplication.usecases;

import br.com.pitflow.registry.domain.Vehicle;

import java.util.List;
import java.util.UUID;

public interface FindVehiclesByCustomerId {
    List<Vehicle> execute(UUID customerId);
}
