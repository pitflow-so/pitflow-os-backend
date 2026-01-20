package br.com.pitflow.registry.infrastructure.persistence.repository;

import br.com.pitflow.registry.infrastructure.persistence.entity.MechanicJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringMechanicRepository extends JpaRepository<MechanicJpa, UUID> {
    Optional<MechanicJpa> findByUsername(String username);
}