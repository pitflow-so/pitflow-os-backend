package br.com.pitflow.inventory.infrastructure.persistence.mapper;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.infrastructure.persistence.entity.ServiceJpa;

public final class ServiceMapper {

    private ServiceMapper() {
    }

    public static ServiceJpa toEntity(Service domain) {
        if (domain == null) {
            return null;
        }

        return new ServiceJpa(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice()
        );
    }

    public static Service toDomain(ServiceJpa jpa) {
        if (jpa == null) {
            return null;
        }

        var service = new Service(
                jpa.getName(),
                jpa.getDescription(),
                jpa.getPrice()
        );

        // Seta o ID que veio do banco, porque o damin sempre gera um novo
        service.setId(jpa.getId());

        return service;
    }
}