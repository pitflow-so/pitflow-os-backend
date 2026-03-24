package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.infrastructure.persistence.entity.VehicleJpa;

public class VehicleMapper {
    private VehicleMapper() {}

    public static VehicleJpa toEntity(Vehicle domain) {
        return new VehicleJpa(
                domain.getId(),
                domain.getCustomerId(),
                domain.getLicensePlate().value(),
                domain.getBrand(),
                domain.getModel(),
                domain.getYear()
        );
    }

    public static Vehicle toDomain(VehicleJpa entity) {
        var vehicle = new Vehicle(
                entity.getCustomerId(),
                new LicensePlate(entity.getLicensePlate()),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear()
        );

        // O construto do domínio gera um ID novo, então precisamos setar o ID correto aqui
        vehicle.setId(entity.getId());
        return vehicle;
    }
}
