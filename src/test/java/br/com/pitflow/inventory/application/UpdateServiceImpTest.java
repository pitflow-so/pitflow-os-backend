package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.UpdateServiceDto;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UpdateServiceImpTest {
    private ServiceGateway repository;
    private UpdateServiceImp updateService;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceGateway.class);
        updateService = new UpdateServiceImp(repository);
    }

    @Test
    @DisplayName("Should update service successfully")
    void shouldUpdateServiceSuccessfully() {
        UUID id = UUID.randomUUID();
        var service = new Service("Old", "Desc", new BigDecimal("50"));
        var dto = new UpdateServiceDto("New", "New Desc", new BigDecimal("100"));

        when(repository.findById(id)).thenReturn(Optional.of(service));

        updateService.execute(id, dto);

        assertThat(service.getName()).isEqualTo("New");
        assertThat(service.getPrice()).isEqualTo(new BigDecimal("100"));
        verify(repository).save(service);
    }
}