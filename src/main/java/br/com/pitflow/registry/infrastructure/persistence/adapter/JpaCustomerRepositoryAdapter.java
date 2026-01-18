package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.infrastructure.persistence.mapper.CustomerMapper;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringCustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaCustomerRepositoryAdapter implements CustomerRepository {
    private final SpringCustomerRepository repository;

    public JpaCustomerRepositoryAdapter(SpringCustomerRepository repository) {
        this.repository = repository;
    }
    @Override
    public void save(Customer customer) {
        var entity = CustomerMapper.toEntity(customer);
        repository.save(entity);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return repository.findById(id)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByDocument(CpfCnpj document) {
        return repository.findByDocument(document.value())
                .map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll().stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
