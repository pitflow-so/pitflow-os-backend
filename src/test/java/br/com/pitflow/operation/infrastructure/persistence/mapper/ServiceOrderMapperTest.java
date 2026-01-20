package br.com.pitflow.operation.infrastructure.persistence.mapper;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.infrastructure.persistence.entity.OrderItemJpa;
import br.com.pitflow.operation.infrastructure.persistence.entity.ServiceOrderJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderMapperTest {

    @Test
    @DisplayName("Should map service order domain to JPA correctly")
    void shouldMapDomainToJpaCorrectly() {
        // Arrange
        var customerId = UUID.randomUUID();
        var vehicleId = UUID.randomUUID();

        var domain = new ServiceOrder(
                customerId,
                vehicleId,
                "Revisão completa"
        );

        domain.addService(
                UUID.randomUUID(),
                "Troca de óleo",
                new BigDecimal("120.00")
        );

        domain.addPart(
                UUID.randomUUID(),
                "Filtro de óleo",
                new BigDecimal("45.00"),
                1
        );

        // Act
        var jpa = ServiceOrderMapper.toJpa(domain);

        // Assert
        assertThat(jpa).isNotNull();
        assertThat(jpa.getId()).isEqualTo(domain.getId());
        assertThat(jpa.getCustomerId()).isEqualTo(customerId);
        assertThat(jpa.getVehicleId()).isEqualTo(vehicleId);
        assertThat(jpa.getDescription()).isEqualTo("Revisão completa");
        assertThat(jpa.getStatus()).isEqualTo(domain.getStatus());
        assertThat(jpa.getItems()).hasSize(2);
        assertThat(jpa.getCreatedAt()).isEqualTo(domain.getCreatedAt());
    }

    @Test
    @DisplayName("Should map service order JPA to domain correctly")
    void shouldMapJpaToDomainCorrectly() {
        // Arrange
        var id = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var vehicleId = UUID.randomUUID();

        var createdAt = LocalDateTime.now().minusDays(2);
        var diagnosisAt = LocalDateTime.now().minusDays(1);
        var executionAt = LocalDateTime.now().minusHours(10);
        var finishedAt = LocalDateTime.now().minusHours(2);
        var deliveredAt = LocalDateTime.now().minusHours(1);

        var items = List.of(
                new OrderItemJpa(
                        UUID.randomUUID(),
                        "Troca de óleo",
                        new BigDecimal("120.00"),
                        1,
                        ServiceOrder.ItemType.SERVICE
                )
        );

        var jpa = new ServiceOrderJpa(
                id,
                customerId,
                vehicleId,
                "Revisão completa",
                ServiceOrder.Status.FINISHED,
                items,
                createdAt,
                diagnosisAt,
                executionAt,
                finishedAt,
                deliveredAt,
                null,
                null
        );

        // Act
        var domain = ServiceOrderMapper.toDomain(jpa);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getCustomerId()).isEqualTo(customerId);
        assertThat(domain.getVehicleId()).isEqualTo(vehicleId);
        assertThat(domain.getDescription()).isEqualTo("Revisão completa");
        assertThat(domain.getStatus()).isEqualTo(ServiceOrder.Status.FINISHED);

        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        assertThat(domain.getDiagnosisStartedAt()).isEqualTo(diagnosisAt);
        assertThat(domain.getExecutionStartedAt()).isEqualTo(executionAt);
        assertThat(domain.getFinishedAt()).isEqualTo(finishedAt);
        assertThat(domain.getDeliveredAt()).isEqualTo(deliveredAt);

        assertThat(domain.getItems()).hasSize(1);
        assertThat(domain.getItems().get(0).description()).isEqualTo("Troca de óleo");
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void shouldReturnNullWhenDomainIsNull() {
        // Arrange
        ServiceOrder domain = null;

        // Act
        var result = ServiceOrderMapper.toJpa(domain);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when JPA entity is null")
    void shouldReturnNullWhenJpaIsNull() {
        // Arrange
        ServiceOrderJpa jpa = null;

        // Act
        var result = ServiceOrderMapper.toDomain(jpa);

        // Assert
        assertThat(result).isNull();
    }
}
