package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;
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

    private PartRepository repository;
    private FindPartBySkuImp findPartBySku;

    @BeforeEach
    void setUp() {
        repository = mock(PartRepository.class);
        findPartBySku = new FindPartBySkuImp(repository);
    }

    @Test
    @DisplayName("Should return part when SKU exists")
    void shouldReturnPartWhenSkuExists() {
        // Arrange
        String sku = "FIL-999";
        var part = new Part(sku, "Filtro de Ar", "Desc", new BigDecimal("45.00"), 5);

        when(repository.findBySku(sku)).thenReturn(Optional.of(part));

        // Act
        Part result = findPartBySku.execute(sku);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo(sku);
        assertThat(result.getName()).isEqualTo("Filtro de Ar");
        verify(repository, times(1)).findBySku(sku);
    }

    @Test
    @DisplayName("Should throw exception when SKU not found")
    void shouldThrowExceptionWhenSkuNotFound() {
        // Arrange
        String sku = "INEXISTENTE-001";
        when(repository.findBySku(sku)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> findPartBySku.execute(sku))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Part with SKU " + sku + " not found.");

        verify(repository, times(1)).findBySku(sku);
    }
}
