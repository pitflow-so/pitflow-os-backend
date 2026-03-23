package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.List;
import java.util.UUID;

public interface FindVehiclesByCustomerId {
    List<Vehicle> execute(UUID customerId);
}
