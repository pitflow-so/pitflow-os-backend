package br.com.pitflow.registry.domain.repository;

import br.com.pitflow.common.valueobject.LicensePlate;
import br.com.pitflow.registry.domain.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {

    void save(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    Optional<Vehicle> findByLicensePlate(LicensePlate licensePlate);
    List<Vehicle> findByCustomerId(UUID customerId);
    void deleteById(UUID id);
    List<Vehicle> findAll();
}
