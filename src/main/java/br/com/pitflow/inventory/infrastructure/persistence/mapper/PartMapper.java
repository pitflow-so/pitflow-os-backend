package br.com.pitflow.inventory.infrastructure.persistence.mapper;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.infrastructure.persistence.entity.PartJpa;

public final class PartMapper {

    private PartMapper() {
    }

    public static PartJpa toEntity(Part domain) {
        if (domain == null) {
            return null;
        }

        return new PartJpa(
                domain.getId(),
                domain.getSku(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getStockQuantity()
        );
    }

    public static Part toDomain(PartJpa jpa) {
        if (jpa == null) {
            return null;
        }

        var part = new Part(
                jpa.getSku(),
                jpa.getName(),
                jpa.getDescription(),
                jpa.getPrice(),
                jpa.getStockQuantity()
        );

        // Seta o ID que veio do banco, pois o construtor do domínio gera um novo UUID
        part.setId(jpa.getId());

        return part;
    }
}