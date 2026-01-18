package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.infrastructure.persistence.entity.CustomerJpa;

import java.time.LocalDateTime;

public final class CustomerMapper {

    private CustomerMapper(){}

    public static CustomerJpa toEntity(Customer domain) {
        if (domain == null) return null;

        return new CustomerJpa(
                domain.getId(),
                domain.getName(),
                domain.getDocument().value(),
                domain.getPhone(),
                LocalDateTime.now()
        );
    }

    public static Customer toDomain(CustomerJpa entity) {
        if (entity == null) return null;
        Customer domain = new Customer(
                entity.getName(),
                new CpfCnpj(entity.getDocument()),
                entity.getPhone()
        );

        // Importante sempre utilizar o setId para manter a consistência do ID
        // entre a entidade JPA e o domínio
        domain.setId(entity.getId());
        return domain;
    }
}
