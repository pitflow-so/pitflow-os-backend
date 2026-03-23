package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FindPartBySkuImpTest {

    private PartGateway gateway;
    private FindPartBySkuImp findPartBySku;

    @BeforeEach
    void setUp() {
        gateway = mock(PartGateway.class);
        findPartBySku = new FindPartBySkuImp(gateway);
    }

    @Test
    @DisplayName("Should return part when SKU exists")
    void shouldReturnPartWhenSkuExists() {
        // Arrange
        String sku = "FIL-999";
        var part = new Part(sku, "Filtro de Ar", "Desc", new BigDecimal("45.00"), 5);

        when(gateway.findBySku(sku)).thenReturn(Optional.of(part));

        // Act
        Part result = findPartBySku.execute(sku);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo(sku);
        assertThat(result.getName()).isEqualTo("Filtro de Ar");
        verify(gateway, times(1)).findBySku(sku);
    }

    @Test
    @DisplayName("Should throw exception when SKU not found")
    void shouldThrowExceptionWhenSkuNotFound() {
        // Arrange
        String sku = "INEXISTENTE-001";
        when(gateway.findBySku(sku)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findPartBySku.execute(sku))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Part with SKU " + sku + " not found.");

        verify(gateway, times(1)).findBySku(sku);
    }
}
