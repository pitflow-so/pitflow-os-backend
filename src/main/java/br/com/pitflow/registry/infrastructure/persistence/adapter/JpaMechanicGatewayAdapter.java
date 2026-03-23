package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import br.com.pitflow.registry.infrastructure.persistence.mapper.MechanicMapper;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringMechanicRepository;

import java.util.Optional;
import java.util.UUID;

public class JpaMechanicGatewayAdapter implements MechanicGateway {
    private final SpringMechanicRepository springRepository;

    public JpaMechanicGatewayAdapter(SpringMechanicRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public void save(Mechanic mechanic) {
        springRepository.save(MechanicMapper.toJpa(mechanic));
    }

    @Override
    public Optional<Mechanic> findByUsername(String username) {
        return springRepository.findByUsername(username).map(MechanicMapper::toDomain);
    }

    @Override
    public Optional<Mechanic> findById(UUID id) {
        return springRepository.findById(id).map(MechanicMapper::toDomain);
    }
}