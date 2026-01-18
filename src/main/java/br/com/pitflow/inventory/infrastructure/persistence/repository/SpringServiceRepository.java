package br.com.pitflow.inventory.infrastructure.persistence.repository;

import br.com.pitflow.inventory.infrastructure.persistence.entity.ServiceJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringServiceRepository extends JpaRepository<ServiceJpa, UUID> {
}