package br.com.pitflow.inventory.infrastructure.persistence.adapter;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.inventory.infrastructure.persistence.mapper.ServiceMapper;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringServiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaServiceGatewayAdapter implements ServiceGateway {

    private final SpringServiceRepository springServiceRepository;

    public JpaServiceGatewayAdapter(SpringServiceRepository springServiceRepository) {
        this.springServiceRepository = springServiceRepository;
    }

    @Override
    public void save(Service service) {
        var entity = ServiceMapper.toEntity(service);
        springServiceRepository.save(entity);
    }

    @Override
    public Optional<Service> findById(UUID id) {
        return springServiceRepository.findById(id)
                .map(ServiceMapper::toDomain);
    }

    @Override
    public List<Service> findAll() {
        return springServiceRepository.findAll().stream()
                .map(ServiceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springServiceRepository.deleteById(id);
    }
}