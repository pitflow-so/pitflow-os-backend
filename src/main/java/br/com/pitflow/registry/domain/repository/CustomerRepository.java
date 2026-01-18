package br.com.pitflow.registry.domain.repository;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.domain.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    void save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByDocument(CpfCnpj document);

    List<Customer> findAll();

    void delete(UUID id);

    void deleteById(UUID id);
}
