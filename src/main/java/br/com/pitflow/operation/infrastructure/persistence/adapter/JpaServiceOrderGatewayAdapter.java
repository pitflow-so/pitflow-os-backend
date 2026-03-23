package br.com.pitflow.operation.infrastructure.persistence.adapter;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.infrastructure.persistence.entity.ServiceOrderJpa;
import br.com.pitflow.operation.infrastructure.persistence.mapper.ServiceOrderMapper;
import br.com.pitflow.operation.infrastructure.persistence.repository.SpringServiceOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaServiceOrderGatewayAdapter implements ServiceOrderGateway {

    private final SpringServiceOrderRepository springRepository;

    public JpaServiceOrderGatewayAdapter(SpringServiceOrderRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public void save(ServiceOrder serviceOrder) {
        ServiceOrderJpa jpaEntity = ServiceOrderMapper.toJpa(serviceOrder);
        springRepository.save(jpaEntity);
    }

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
        return springRepository.findById(id)
                .map(ServiceOrderMapper::toDomain);
    }

    @Override
    public List<ServiceOrder> findAll() {
        return springRepository.findAll().stream()
                .map(ServiceOrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceOrder> findAllByOrderByCreatedAtAsc() {
        return springRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(ServiceOrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceOrder> findByCustomerId(UUID customerId) {
        return springRepository.findByCustomerId(customerId).stream()
                .map(ServiceOrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceOrder> findByVehicleId(UUID vehicleId) {
        return springRepository.findByVehicleId(vehicleId).stream()
                .map(ServiceOrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceOrder> findByStatusOrderByCreatedAtAsc(ServiceOrder.Status status) {
        return springRepository.findByStatusOrderByCreatedAtAsc(status).stream()
                .map(ServiceOrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageExecutionTimeInSeconds() {
        return springRepository.getAverageExecutionTimeInSeconds();
    }
}