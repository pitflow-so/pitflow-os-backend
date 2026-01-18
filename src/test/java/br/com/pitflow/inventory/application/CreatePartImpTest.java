package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.CreatePartDto;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreatePartImpTest {

    private PartRepository repository;
    private CreatePartImp createPart;

    @BeforeEach
    void setUp() {
        repository = mock(PartRepository.class);
        createPart = new CreatePartImp(repository);
    }

    @Test
    @DisplayName("Should create part successfully")
    void shouldCreatePartSuccessfully() {
        var dto = new CreatePartDto("SKU-1", "Peça", "Desc", new BigDecimal("10.00"), 5);
        when(repository.findBySku("SKU-1")).thenReturn(Optional.empty());

        var result = createPart.execute(dto);

        assertThat(result.getSku()).isEqualTo("SKU-1");
        verify(repository, times(1)).save(any(Part.class));
    }

    @Test
    @DisplayName("Should throw error when SKU already exists")
    void shouldThrowErrorWhenSkuExists() {
        var dto = new CreatePartDto("SKU-1", "Peça", "Desc", new BigDecimal("10.00"), 5);
        var existingPart = new Part("SKU-1", "Antiga", "D", new BigDecimal("5.00"), 1);

        when(repository.findBySku("SKU-1")).thenReturn(Optional.of(existingPart));

        assertThatThrownBy(() -> createPart.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).save(any(Part.class));
    }
}
