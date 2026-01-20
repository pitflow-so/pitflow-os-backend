package br.com.pitflow.operation.infrastructure.persistence.mapper;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.infrastructure.persistence.entity.OrderItemJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemMapperTest {

    @Test
    @DisplayName("Should map domain item to JPA correctly")
    void shouldMapDomainToJpaCorrectly() {
        // Arrange
        var catalogId = UUID.randomUUID();
        var domainItem = new ServiceOrder.Item(
                catalogId,
                "Troca de óleo",
                new BigDecimal("120.00"),
                1,
                ServiceOrder.ItemType.SERVICE
        );

        // Act
        var jpaItem = OrderItemMapper.toJpa(domainItem);

        // Assert
        assertThat(jpaItem).isNotNull();
        assertThat(jpaItem.getCatalogId()).isEqualTo(catalogId);
        assertThat(jpaItem.getDescription()).isEqualTo("Troca de óleo");
        assertThat(jpaItem.getUnitPrice()).isEqualByComparingTo("120.00");
        assertThat(jpaItem.getQuantity()).isEqualTo(1);
        assertThat(jpaItem.getType()).isEqualTo(ServiceOrder.ItemType.SERVICE);
    }

    @Test
    @DisplayName("Should map JPA item to domain correctly")
    void shouldMapJpaToDomainCorrectly() {
        // Arrange
        var catalogId = UUID.randomUUID();
        var jpaItem = new OrderItemJpa(
                catalogId,
                "Troca de óleo",
                new BigDecimal("120.00"),
                1,
                ServiceOrder.ItemType.SERVICE
        );

        // Act
        var domainItem = OrderItemMapper.toDomain(jpaItem);

        // Assert
        assertThat(domainItem).isNotNull();
        assertThat(domainItem.catalogId()).isEqualTo(catalogId);
        assertThat(domainItem.description()).isEqualTo("Troca de óleo");
        assertThat(domainItem.unitPrice()).isEqualByComparingTo("120.00");
        assertThat(domainItem.quantity()).isEqualTo(1);
        assertThat(domainItem.type()).isEqualTo(ServiceOrder.ItemType.SERVICE);
    }

    @Test
    @DisplayName("Should return null when domain item is null")
    void shouldReturnNullWhenDomainIsNull() {
        // Arrange
        ServiceOrder.Item domainItem = null;

        // Act
        var result = OrderItemMapper.toJpa(domainItem);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when JPA item is null")
    void shouldReturnNullWhenJpaIsNull() {
        // Arrange
        OrderItemJpa jpaItem = null;

        // Act
        var result = OrderItemMapper.toDomain(jpaItem);

        // Assert
        assertThat(result).isNull();
    }
}
