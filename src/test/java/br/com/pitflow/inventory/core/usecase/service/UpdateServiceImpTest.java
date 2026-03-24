package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.controller.dto.UpdateServiceCommand;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdateServiceRequest;
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
    private ServiceGateway gateway;
    private UpdateServiceImp updateService;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceGateway.class);
        updateService = new UpdateServiceImp(gateway);
    }

    @Test
    @DisplayName("Should update service successfully")
    void shouldUpdateServiceSuccessfully() {
        UUID id = UUID.randomUUID();
        var service = new Service("Old", "Desc", new BigDecimal("50"));
        var dto = new UpdateServiceCommand("New", "New Desc", new BigDecimal("100"));

        when(gateway.findById(id)).thenReturn(Optional.of(service));

        updateService.execute(id, dto);

        assertThat(service.getName()).isEqualTo("New");
        assertThat(service.getPrice()).isEqualTo(new BigDecimal("100"));
        verify(gateway).save(service);
    }
}