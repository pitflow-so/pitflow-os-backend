package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.infrastructure.persistence.mapper.VehicleMapper;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringVehicleRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaVehicleGatewayAdapter implements VehicleGateway {

    private final SpringVehicleRepository repository;

    public JpaVehicleGatewayAdapter(SpringVehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Vehicle vehicle) {
        var entity = VehicleMapper.toEntity(vehicle);
        repository.save(entity);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return repository.findById(id)
                .map(VehicleMapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findByLicensePlate(LicensePlate licensePlate) {
        return repository.findByLicensePlate(licensePlate.value())
                .map(VehicleMapper::toDomain);
    }

    @Override
    public List<Vehicle> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(VehicleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<Vehicle> findAll() {
        return repository.findAll().stream()
                .map(VehicleMapper::toDomain)
                .collect(Collectors.toList());
    }
}
