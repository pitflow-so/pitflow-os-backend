package br.com.pitflow.registry.domain.repository;

import br.com.pitflow.registry.domain.Mechanic;

import java.util.Optional;
import java.util.UUID;

public interface MechanicRepository {
    void save(Mechanic mechanic);
    Optional<Mechanic> findByUsername(String username);
    Optional<Mechanic> findById(UUID id);
}
