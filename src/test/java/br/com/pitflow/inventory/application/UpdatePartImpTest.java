package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.UpdatePartDto;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdatePartImpTest {
    private PartRepository repository;
    private UpdatePartImp updatePart;

    @BeforeEach void setUp() {
        repository = mock(PartRepository.class);
        updatePart = new UpdatePartImp(repository);
    }

    @Test
    @DisplayName("Should update part successfully")
    void shouldUpdatePartSuccessfully() {
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-OLD", "Old Name", "Desc", new BigDecimal("10"), 1);
        part.setId(id);
        var dto = new UpdatePartDto("SKU-NEW", "New Name", "New Desc", new BigDecimal("20"), 5);

        when(repository.findById(id)).thenReturn(Optional.of(part));
        when(repository.findBySku("SKU-NEW")).thenReturn(Optional.empty());

        updatePart.execute(id, dto);

        assertThat(part.getSku()).isEqualTo("SKU-NEW");
        assertThat(part.getPrice()).isEqualTo(new BigDecimal("20"));
        verify(repository).save(part);
    }

    @Test
    @DisplayName("Should throw exception when SKU already in use by another part")
    void shouldThrowExceptionWhenSkuAlreadyInUseByAnotherPart() {
        UUID id = UUID.randomUUID();
        var part = new Part("SKU-1", "Name", "Desc", new BigDecimal("10"), 1);
        var dto = new UpdatePartDto("SKU-TAKEN", "Name", "Desc", new BigDecimal("10"), 1);
        var anotherPart = new Part("SKU-TAKEN", "Other", "Desc", new BigDecimal("10"), 1);

        when(repository.findById(id)).thenReturn(Optional.of(part));
        when(repository.findBySku("SKU-TAKEN")).thenReturn(Optional.of(anotherPart));

        assertThatThrownBy(() -> updatePart.execute(id, dto))
                .isInstanceOf(IllegalStateException.class);
    }
}
