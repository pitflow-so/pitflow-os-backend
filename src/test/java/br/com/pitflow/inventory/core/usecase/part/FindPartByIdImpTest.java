package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindPartByIdImpTest {

    private PartGateway gateway;
    private FindPartByIdImp findPartById;

    @BeforeEach
    void setUp() {
        gateway = mock(PartGateway.class);
        findPartById = new FindPartByIdImp(gateway);
    }

    @Test
    @DisplayName("Should return part when ID exists")
    void shouldReturnPartWhenIdExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-123", "Pastilha", "Desc", new BigDecimal("100.00"), 10);
        part.setId(id);

        when(gateway.findById(id)).thenReturn(Optional.of(part));

        // Act
        Part result = findPartById.execute(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getSku()).isEqualTo("SKU-123");
        verify(gateway, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when part not found")
    void shouldThrowExceptionWhenPartNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findPartById.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Part not found with ID: " + id);

        verify(gateway, times(1)).findById(id);
    }
}
