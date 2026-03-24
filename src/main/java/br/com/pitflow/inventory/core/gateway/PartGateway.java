package br.com.pitflow.inventory.core.gateway;

import br.com.pitflow.inventory.core.entity.Part;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartGateway {
    void save(Part part);
    Optional<Part> findById(UUID id);
    Optional<Part> findBySku(String sku);
    List<Part> findAll();
    void deleteById(UUID id);
}