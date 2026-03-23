package br.com.pitflow.inventory.core.gateway;

import br.com.pitflow.inventory.core.entity.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceGateway {
    void save(Service service);
    Optional<Service> findById(UUID id);
    List<Service> findAll();
    void deleteById(UUID id);
}