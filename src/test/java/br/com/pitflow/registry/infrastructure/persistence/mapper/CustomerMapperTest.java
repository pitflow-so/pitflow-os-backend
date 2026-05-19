package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.valueObject.Email;
import br.com.pitflow.registry.infrastructure.persistence.entity.CustomerJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    @Test
    @DisplayName("Should map domain to JPA entity correctly")
    void shouldMapDomainToEntity() {
        // Arrange
        var id = UUID.randomUUID();
        var domain = new Customer(
                "João Silva",
                "11999999999",
                new Email("joao@gmail.com"),
                new CpfCnpj("06678477073")
        );
        domain.setId(id);

        // Act
        var entity = CustomerMapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("João Silva");
        assertThat(entity.getDocument()).isEqualTo("06678477073");
        assertThat(domain.getEmail().value()).isEqualTo("joao@gmail.com");
        assertThat(entity.getPhone()).isEqualTo("11999999999");
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should map JPA entity to domain correctly")
    void shouldMapEntityToDomain() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new CustomerJpa(
                id,
                "João Silva",
                "06678477073",
                "11999999999",
                "joao@gmail.com",
                "ACTIVE",
                LocalDateTime.now()
        );

        // Act
        var domain = CustomerMapper.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("João Silva");
        assertThat(domain.getDocument().value()).isEqualTo("06678477073");
        assertThat(domain.getEmail().value()).isEqualTo("joao@gmail.com");
        assertThat(domain.getPhone()).isEqualTo("11999999999");
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(CustomerMapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(CustomerMapper.toDomain(null)).isNull();
    }
}