package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.valueObject.Email;
import br.com.pitflow.registry.infrastructure.persistence.entity.CustomerJpa;

import java.time.LocalDateTime;

public final class CustomerMapper {

    private CustomerMapper(){}

    public static CustomerJpa toEntity(Customer domain) {
        if (domain == null) return null;

        CustomerJpa entity = new CustomerJpa(
                domain.getId(),
                domain.getName(),
                domain.getDocument().value(),
                domain.getPhone(),
                domain.getEmail().value(),
                domain.geStatus(),
                LocalDateTime.now()
        );

        return entity;
    }

    public static Customer toDomain(CustomerJpa entity) {
        if (entity == null) return null;
        Customer domain = new Customer(
                entity.getName(),
                entity.getPhone(),
                new Email(entity.getEmail()),
                new CpfCnpj(entity.getDocument())
        );

        // Importante sempre utilizar o setId para manter a consistência do ID
        // entre a entidade JPA e o domínio
        domain.setId(entity.getId());
        domain.setStatus(entity.getStatus());

        return domain;
    }
}
