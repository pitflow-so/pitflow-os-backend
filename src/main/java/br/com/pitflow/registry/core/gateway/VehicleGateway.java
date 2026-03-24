package br.com.pitflow.registry.core.gateway;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleGateway {

    void save(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    Optional<Vehicle> findByLicensePlate(LicensePlate licensePlate);
    List<Vehicle> findByCustomerId(UUID customerId);
    void deleteById(UUID id);
    List<Vehicle> findAll();
}
