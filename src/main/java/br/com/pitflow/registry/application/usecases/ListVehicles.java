package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.domain.Vehicle;

import java.util.List;

public interface ListVehicles {
    List<Vehicle> execute();
}
