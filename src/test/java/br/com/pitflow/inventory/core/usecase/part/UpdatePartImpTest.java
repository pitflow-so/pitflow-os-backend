package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.controller.dto.UpdatePartCommand;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdatePartImpTest {
    private PartGateway gateway;
    private UpdatePartImp updatePart;

    @BeforeEach void setUp() {
        gateway = mock(PartGateway.class);
        updatePart = new UpdatePartImp(gateway);
    }

    @Test
    @DisplayName("Should update part successfully")
    void shouldUpdatePartSuccessfully() {
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-OLD", "Old Name", "Desc", new BigDecimal("10"), 1);
        part.setId(id);
        var dto = new UpdatePartCommand("SKU-NEW", "New Name", "New Desc", new BigDecimal("20"), 5);

        when(gateway.findById(id)).thenReturn(Optional.of(part));
        when(gateway.findBySku("SKU-NEW")).thenReturn(Optional.empty());

        updatePart.execute(id, dto);

        assertThat(part.getSku()).isEqualTo("SKU-NEW");
        assertThat(part.getPrice()).isEqualTo(new BigDecimal("20"));
        verify(gateway).save(part);
    }

    @Test
    @DisplayName("Should throw exception when SKU already in use by another part")
    void shouldThrowExceptionWhenSkuAlreadyInUseByAnotherPart() {
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-1", "Name", "Desc", new BigDecimal("10"), 1);
        var dto = new UpdatePartCommand("SKU-TAKEN", "Name", "Desc", new BigDecimal("10"), 1);
        var anotherPart = new Part("SKU-TAKEN", "Other", "Desc", new BigDecimal("10"), 1);

        when(gateway.findById(id)).thenReturn(Optional.of(part));
        when(gateway.findBySku("SKU-TAKEN")).thenReturn(Optional.of(anotherPart));

        assertThatThrownBy(() -> updatePart.execute(id, dto))
                .isInstanceOf(IllegalStateException.class);
    }
}
