package br.com.pitflow.inventory.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PartTest {
    @Test
    @DisplayName("Should create a part with initial stock")
    void shouldCreatePart() {
        var part = new Part("SKU-123","Pastilha de freio", "Front brake pads for Toyota Corolla 2000 a 2010", new BigDecimal("250.00"), 10);
        part.setId(UUID.randomUUID());

        assertThat(part.getName()).isEqualTo("Pastilha de freio");
        assertThat(part.getStockQuantity()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception for invalid SKU")
    void shouldThrowExceptionForInvalidSku(String invalidSku) {
        assertThatThrownBy(() ->
                new Part(invalidSku, "Name", "Desc", new BigDecimal("10.00"), 5)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Part SKU cannot be empty.");
    }

    @Test
    @DisplayName("Should throw exception for null SKU")
    void shouldThrowExceptionForNullSku() {
        assertThatThrownBy(() ->
                new Part(null, "Name", "Desc", new BigDecimal("10.00"), 5)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Part SKU cannot be empty.");
    }

    @Test
    @DisplayName("Should add stock successfully")
    void shouldAddStock() {
        var part = new Part("SKU-123", "Filtro de óleo", "Filtro de óleo sintético", new BigDecimal("50.00"), 5);

        part.addStock(10);

        assertThat(part.getStockQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should remove stock successfully")
    void shouldRemoveStock() {
        var part = new Part("SKU-123", "Vela de Ignição", "Vela de ignição de irídio", new BigDecimal("80.00"), 20);

        part.removeStock(5);

        assertThat(part.getStockQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should throw exception when removing more than available stock")
    void shouldThrowExceptionForInsufficientStock() {
        var part = new Part("SKU-123", "Pneu", "pirelli p7 205 55 r16", new BigDecimal("600.00"), 2);

        assertThatThrownBy(() -> part.removeStock(5)).isInstanceOf(IllegalStateException.class).hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should throw exception for invalid price")
    void shouldThrowExceptionForInvalidPrice() {
        assertThatThrownBy(() -> new Part("SKU-123", "dummy", "Description", new BigDecimal("-10.00"), 10)).isInstanceOf(IllegalArgumentException.class).hasMessage("Part price must be greater than zero.");
    }
}
