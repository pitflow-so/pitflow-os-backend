package br.com.pitflow.inventory.infrastructure.persistence.mapper;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.infrastructure.persistence.entity.PartJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PartMapperTest {

    @Test
    @DisplayName("Should map part domain to JPA entity correctly")
    void shouldMapDomainToEntityCorrectly() {
        // Arrange
        var id = UUID.randomUUID();

        var domain = new Part(
                "SKU-001",
                "Filtro de óleo",
                "Filtro de óleo para motor 1.6",
                new BigDecimal("45.90"),
                10
        );
        domain.setId(id);

        // Act
        var entity = PartMapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getSku()).isEqualTo("SKU-001");
        assertThat(entity.getName()).isEqualTo("Filtro de óleo");
        assertThat(entity.getDescription()).isEqualTo("Filtro de óleo para motor 1.6");
        assertThat(entity.getPrice()).isEqualByComparingTo("45.90");
        assertThat(entity.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should map JPA entity to part domain correctly")
    void shouldMapJpaToDomainCorrectly() {
        // Arrange
        var id = UUID.randomUUID();

        var jpa = new PartJpa(
                id,
                "SKU-001",
                "Filtro de óleo",
                "Filtro de óleo para motor 1.6",
                new BigDecimal("45.90"),
                10
        );

        // Act
        var domain = PartMapper.toDomain(jpa);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getSku()).isEqualTo("SKU-001");
        assertThat(domain.getName()).isEqualTo("Filtro de óleo");
        assertThat(domain.getDescription()).isEqualTo("Filtro de óleo para motor 1.6");
        assertThat(domain.getPrice()).isEqualByComparingTo("45.90");
        assertThat(domain.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void shouldReturnNullWhenDomainIsNull() {
        // Arrange
        Part domain = null;

        // Act
        var result = PartMapper.toEntity(domain);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when JPA entity is null")
    void shouldReturnNullWhenJpaIsNull() {
        // Arrange
        PartJpa jpa = null;

        // Act
        var result = PartMapper.toDomain(jpa);

        // Assert
        assertThat(result).isNull();
    }
}
