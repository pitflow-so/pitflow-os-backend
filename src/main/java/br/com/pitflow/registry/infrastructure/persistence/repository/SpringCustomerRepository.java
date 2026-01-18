package br.com.pitflow.registry.infrastructure.persistence.repository;

import br.com.pitflow.registry.infrastructure.persistence.entity.CustomerJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringCustomerRepository extends JpaRepository<CustomerJpa, UUID> {
    Optional<CustomerJpa> findByDocument(String document);
}
