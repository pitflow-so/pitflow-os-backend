package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.usecase.part.ListPartsImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListPartsImpTest {

    private PartGateway repository;
    private ListPartsImp listParts;

    @BeforeEach
    void setUp() {
        repository = mock(PartGateway.class);
        listParts = new ListPartsImp(repository);
    }

    @Test
    @DisplayName("Should return all parts")
    void shouldReturnAllParts() {
        // Arrange
        var part1 = new Part("SKU-01", "Filtro de Óleo", "Desc", new BigDecimal("50.00"), 10);
        var part2 = new Part("SKU-02", "Pastilha Freio", "Desc", new BigDecimal("120.00"), 5);
        var partsList = List.of(part1, part2);

        when(repository.findAll()).thenReturn(partsList);

        // Act
        List<Part> result = listParts.execute();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(part1, part2);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no parts exist")
    void shouldReturnEmptyListWhenNoPartsExist() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Part> result = listParts.execute();

        // Assert
        assertThat(result).isEmpty();
        verify(repository, times(1)).findAll();
    }
}