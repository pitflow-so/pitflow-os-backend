package br.com.pitflow.registry.core.gateway;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerGateway {
    void save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByDocument(CpfCnpj document);

    List<Customer> findAll();

    void delete(UUID id);
}
