package br.com.pitflow.inventory.domain.repository;

import br.com.pitflow.inventory.domain.Part;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartRepository {
    void save(Part part);
    Optional<Part> findById(UUID id);
    Optional<Part> findBySku(String sku);
    List<Part> findAll();
    void deleteById(UUID id);
}