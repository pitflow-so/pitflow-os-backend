package br.com.pitflow.inventory.infrastructure.persistence.adapter;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.infrastructure.persistence.mapper.PartMapper;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringPartRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaPartGatewayAdapter implements PartGateway {
    private final SpringPartRepository springPartRepository;

    public JpaPartGatewayAdapter(SpringPartRepository springPartRepository) {
        this.springPartRepository = springPartRepository;
    }

    @Override
    public void save(Part part) {
        var entity = PartMapper.toEntity(part);
        springPartRepository.save(entity);
    }

    @Override
    public Optional<Part> findById(UUID id) {
        return springPartRepository.findById(id)
                .map(PartMapper::toDomain);
    }

    @Override
    public Optional<Part> findBySku(String sku) {
        return springPartRepository.findBySku(sku)
                .map(PartMapper::toDomain);
    }

    @Override
    public List<Part> findAll() {
        return springPartRepository.findAll().stream()
                .map(PartMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springPartRepository.deleteById(id);
    }
}
