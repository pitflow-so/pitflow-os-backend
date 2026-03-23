package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.controller.dto.CreatePartCommand;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
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

    private PartGateway gateway;
    private CreatePartImp createPart;

    @BeforeEach
    void setUp() {
        gateway = mock(PartGateway.class);
        createPart = new CreatePartImp(gateway);
    }

    @Test
    @DisplayName("Should create part successfully")
    void shouldCreatePartSuccessfully() {
        var dto = new CreatePartCommand("SKU-1", "Peça", "Desc", new BigDecimal("10.00"), 5);
        when(gateway.findBySku("SKU-1")).thenReturn(Optional.empty());

        var result = createPart.execute(dto);

        assertThat(result.getSku()).isEqualTo("SKU-1");
        verify(gateway, times(1)).save(any(Part.class));
    }

    @Test
    @DisplayName("Should throw error when SKU already exists")
    void shouldThrowErrorWhenSkuExists() {
        var dto = new CreatePartCommand("SKU-1", "Peça", "Desc", new BigDecimal("10.00"), 5);
        var existingPart = new Part("SKU-1", "Antiga", "D", new BigDecimal("5.00"), 1);

        when(gateway.findBySku("SKU-1")).thenReturn(Optional.of(existingPart));

        assertThatThrownBy(() -> createPart.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

        verify(gateway, never()).save(any(Part.class));
    }
}
