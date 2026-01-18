package br.com.pitflow.registry.infrastructure.persistence.repository;

import br.com.pitflow.registry.infrastructure.persistence.entity.VehicleJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringVehicleRepository extends JpaRepository<VehicleJpa, UUID> {
    Optional<VehicleJpa> findByLicensePlate(String licensePlate);
    List<VehicleJpa> findByCustomerId(UUID customerId);
}