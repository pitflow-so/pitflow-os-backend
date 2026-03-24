package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeletePartImpTest {

    private PartGateway gateway;
    private DeletePartImp deletePart;

    @BeforeEach
    void setUp() {
        gateway = mock(PartGateway.class);
        deletePart = new DeletePartImp(gateway);
    }

    @Test
    @DisplayName("Should delete part successfully")
    void shouldDeletePartSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-123", "Peça Teste", "Desc", new BigDecimal("10.00"), 5);

        when(gateway.findById(id)).thenReturn(Optional.of(part));

        // Act
        deletePart.execute(id);

        // Assert
        verify(gateway, times(1)).findById(id);
        verify(gateway, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when part not found")
    void shouldThrowExceptionWhenPartNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(gateway.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deletePart.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot delete: Part not found with ID: " + id);

        // Verify
        verify(gateway, never()).deleteById(any());
    }
}