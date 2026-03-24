package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.infrastructure.persistence.entity.MechanicJpa;

public final class MechanicMapper {
    private MechanicMapper() {}

    public static MechanicJpa toJpa(Mechanic domain) {
        return new MechanicJpa(
                domain.getId(),
                domain.getName(),
                domain.getUsername(),
                domain.getPassword(),
                domain.getRole()
        );
    }

    public static Mechanic toDomain(MechanicJpa jpa) {
        var domain = new Mechanic(jpa.getName(), jpa.getUsername(), jpa.getPassword());
        domain.setId(jpa.getId());
        return domain;
    }
}