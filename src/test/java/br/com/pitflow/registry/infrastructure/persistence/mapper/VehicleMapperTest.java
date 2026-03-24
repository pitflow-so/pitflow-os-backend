package br.com.pitflow.registry.infrastructure.persistence.mapper;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.infrastructure.persistence.entity.VehicleJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleMapperTest {

    @Test
    @DisplayName("Should map domain vehicle to JPA entity correctly")
    void shouldMapDomainToEntityCorrectly() {
        // Arrange
        var vehicleId = UUID.randomUUID();
        var customerId = UUID.randomUUID();

        var domain = new Vehicle(
                customerId,
                new LicensePlate("ABC1D23"),
                "Chevrolet",
                "Chevette",
                1973
        );
        domain.setId(vehicleId);

        // Act
        var entity = VehicleMapper.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(vehicleId);
        assertThat(entity.getCustomerId()).isEqualTo(customerId);
        assertThat(entity.getLicensePlate()).isEqualTo("ABC1D23");
        assertThat(entity.getBrand()).isEqualTo("Chevrolet");
        assertThat(entity.getModel()).isEqualTo("Chevette");
        assertThat(entity.getYear()).isEqualTo(1973);
    }

    @Test
    @DisplayName("Should map JPA entity to domain correctly")
    void shouldMapEntityToDomainCorrectly() {
        // Arrange
        var vehicleId = UUID.randomUUID();
        var customerId = UUID.randomUUID();

        var entity = new VehicleJpa(
                vehicleId,
                customerId,
                "ABC1D23",
                "Chevrolet",
                "Chevette",
                1973
        );

        // Act
        var domain = VehicleMapper.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(vehicleId);
        assertThat(domain.getCustomerId()).isEqualTo(customerId);
        assertThat(domain.getLicensePlate().value()).isEqualTo("ABC1D23");
        assertThat(domain.getBrand()).isEqualTo("Chevrolet");
        assertThat(domain.getModel()).isEqualTo("Chevette");
        assertThat(domain.getYear()).isEqualTo(1973);
    }
}