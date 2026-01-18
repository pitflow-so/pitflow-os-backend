package br.com.pitflow.inventory.domain.repository;

import br.com.pitflow.inventory.domain.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository {
    void save(Service service);
    Optional<Service> findById(UUID id);
    List<Service> findAll();
    void deleteById(UUID id);
}