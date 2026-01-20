package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import br.com.pitflow.registry.infrastructure.persistence.mapper.MechanicMapper;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringMechanicRepository;

import java.util.Optional;
import java.util.UUID;

public class JpaMechanicRepositoryAdapter implements MechanicRepository {
    private final SpringMechanicRepository springRepository;

    public JpaMechanicRepositoryAdapter(SpringMechanicRepository springRepository) {
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