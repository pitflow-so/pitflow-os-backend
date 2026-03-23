package br.com.pitflow.registry.core.gateway;

import br.com.pitflow.registry.core.entity.Mechanic;

import java.util.Optional;
import java.util.UUID;

public interface MechanicGateway {
    void save(Mechanic mechanic);
    Optional<Mechanic> findByUsername(String username);
    Optional<Mechanic> findById(UUID id);
}
