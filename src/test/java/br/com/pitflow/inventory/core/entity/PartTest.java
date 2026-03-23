package br.com.pitflow.inventory.core.entity;

import br.com.pitflow.inventory.core.entity.Part;
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

    @Test
    @DisplayName("Should throw exception for invalid names")
    void shouldThrowExceptionForInvalidNames() {
        // Testa nulo
        assertThatThrownBy(() -> new Part("SKU", null, "Desc", new BigDecimal("10"), 1))
                .isInstanceOf(IllegalArgumentException.class);

        // Testa vazio
        assertThatThrownBy(() -> new Part("SKU", "  ", "Desc", new BigDecimal("10"), 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception for zero price")
    void shouldThrowExceptionForZeroPrice() {
        assertThatThrownBy(() -> new Part("SKU", "Name", "Desc", BigDecimal.ZERO, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Part price must be greater than zero.");
    }

    @Test
    @DisplayName("Should throw exception for negative initial stock")
    void shouldThrowExceptionForNegativeInitialStock() {
        assertThatThrownBy(() -> new Part("SKU", "Name", "Desc", new BigDecimal("10"), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Stock quantity cannot be negative.");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5})
    @DisplayName("Should throw exception when adding non-positive quantity to stock")
    void shouldThrowExceptionForInvalidAddStockQuantity(int invalidQuantity) {
        var part = new Part("SKU", "Name", "Desc", new BigDecimal("10"), 5);
        assertThatThrownBy(() -> part.addStock(invalidQuantity))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("Should throw exception when removing non-positive quantity from stock")
    void shouldThrowExceptionForInvalidRemoveStockQuantity(int invalidQuantity) {
        var part = new Part("SKU", "Name", "Desc", new BigDecimal("10"), 5);
        assertThatThrownBy(() -> part.removeStock(invalidQuantity))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should update fields through setters with validation")
    void shouldUpdateFieldsThroughSetters() {
        var part = new Part("SKU", "Name", "Desc", new BigDecimal("10"), 5);

        part.setName("New Name");
        part.setPrice(new BigDecimal("20"));

        assertThat(part.getName()).isEqualTo("New Name");
        assertThat(part.getPrice()).isEqualTo(new BigDecimal("20"));

        // Testa se o setter de preço também valida
        assertThatThrownBy(() -> part.setPrice(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
