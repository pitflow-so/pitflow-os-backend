package br.com.pitflow.inventory.infrastructure.persistence.mapper;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.infrastructure.persistence.entity.ServiceJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceMapperTest {

    @Test
    @DisplayName("Should map service domain to JPA entity correctly")
    void shouldMapDomainToEntityCorrectly() {
        // Arrange
        var id = UUID.randomUUID();

        var domain = new Service(
                "Troca de óleo",
                "Troca de óleo do motor com filtro",
                new BigDecimal("199.90")
        );
        domain.setId(id);

        // Act
        var entity = ServiceMapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Troca de óleo");
        assertThat(entity.getDescription()).isEqualTo("Troca de óleo do motor com filtro");
        assertThat(entity.getPrice()).isEqualByComparingTo("199.90");
    }

    @Test
    @DisplayName("Should map JPA entity to service domain correctly")
    void shouldMapJpaToDomainCorrectly() {
        // Arrange
        var id = UUID.randomUUID();

        var jpa = new ServiceJpa(
                id,
                "Troca de óleo",
                "Troca de óleo do motor com filtro",
                new BigDecimal("199.90")
        );

        // Act
        var domain = ServiceMapper.toDomain(jpa);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Troca de óleo");
        assertThat(domain.getDescription()).isEqualTo("Troca de óleo do motor com filtro");
        assertThat(domain.getPrice()).isEqualByComparingTo("199.90");
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void shouldReturnNullWhenDomainIsNull() {
        // Arrange
        Service domain = null;

        // Act
        var result = ServiceMapper.toEntity(domain);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when JPA entity is null")
    void shouldReturnNullWhenJpaIsNull() {
        // Arrange
        ServiceJpa jpa = null;

        // Act
        var result = ServiceMapper.toDomain(jpa);

        // Assert
        assertThat(result).isNull();
    }
}
