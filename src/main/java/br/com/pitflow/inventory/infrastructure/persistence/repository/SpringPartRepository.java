package br.com.pitflow.inventory.infrastructure.persistence.repository;

import br.com.pitflow.inventory.infrastructure.persistence.entity.PartJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringPartRepository extends JpaRepository<PartJpa, UUID> {
    Optional<PartJpa> findBySku(String sku);
}