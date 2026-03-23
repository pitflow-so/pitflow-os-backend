package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.infrastructure.persistence.entity.MechanicJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MechanicMapperTest {

    @Test
    @DisplayName("Should map domain mechanic to JPA entity correctly")
    void shouldMapDomainToJpaCorrectly() {
        // Arrange
        var id = UUID.randomUUID();
        var domain = new Mechanic("Mestre Splinter", "splinter", "senha123");
        domain.setId(id);

        // Act
        var jpa = MechanicMapper.toJpa(domain);

        // Assert
        assertThat(jpa).isNotNull();
        assertThat(jpa.getId()).isEqualTo(id);
        assertThat(jpa.getName()).isEqualTo("Mestre Splinter");
        assertThat(jpa.getUsername()).isEqualTo("splinter");
        assertThat(jpa.getPassword()).isEqualTo("senha123");
        assertThat(jpa.getRole()).isEqualTo("ROLE_MECHANIC");
    }

    @Test
    @DisplayName("Should map JPA mechanic to domain correctly")
    void shouldMapJpaToDomainCorrectly() {
        // Arrange
        var id = UUID.randomUUID();
        var jpa = new MechanicJpa(
                id,
                "Mestre Splinter",
                "splinter",
                "senha123",
                "ROLE_MECHANIC"
        );

        // Act
        var domain = MechanicMapper.toDomain(jpa);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Mestre Splinter");
        assertThat(domain.getUsername()).isEqualTo("splinter");
        assertThat(domain.getPassword()).isEqualTo("senha123");
        assertThat(domain.getRole()).isEqualTo("ROLE_MECHANIC");
    }
}
