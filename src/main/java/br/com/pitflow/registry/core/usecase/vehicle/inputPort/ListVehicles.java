package br.com.pitflow.registry.core.usecase.vehicle.inputPort;

import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.List;

public interface ListVehicles {
    List<Vehicle> execute();
}
