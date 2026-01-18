package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DeletePartImpTest {

    private PartRepository repository;
    private DeletePartImp deletePart;

    @BeforeEach
    void setUp() {
        repository = mock(PartRepository.class);
        deletePart = new DeletePartImp(repository);
    }

    @Test
    @DisplayName("Should delete part successfully")
    void shouldDeletePartSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-123", "Peça Teste", "Desc", new BigDecimal("10.00"), 5);

        when(repository.findById(id)).thenReturn(Optional.of(part));

        // Act
        deletePart.execute(id);

        // Assert
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when part not found")
    void shouldThrowExceptionWhenPartNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deletePart.execute(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot delete: Part not found with ID: " + id);

        // Verify
        verify(repository, never()).deleteById(any());
    }
}