package br.com.pitflow.inventory.core.usecase.service;

import br.com.pitflow.inventory.controller.dto.CreateServiceCommand;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class CreateServiceImpTest {

    private ServiceGateway gateway;
    private CreateServiceImp createService;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceGateway.class);
        createService = new CreateServiceImp(gateway);
    }

    @Test
    @DisplayName("Should create service successfully")
    void shouldCreateServiceSuccessfully() {
        // Arrange
        var dto = new CreateServiceCommand(
                "Troca de Pastilhas",
                "Substituição das pastilhas de travão dianteiras",
                new BigDecimal("120.00")
        );

        // Act
        Service result = createService.execute(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Troca de Pastilhas");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("120.00"));

        // Verify
        verify(gateway, times(1)).save(any(Service.class));
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Arrange
        var dto = new CreateServiceCommand(
                "Serviço Inválido",
                "Descrição",
                new BigDecimal("-50.00")
        );

        // Act & Assert
        assertThatThrownBy(() -> createService.execute(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service price cannot be negative");

        // Verify
        verify(gateway, never()).save(any(Service.class));
    }

    @Test
    @DisplayName("Should throw exception when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Arrange
        var dto = new CreateServiceCommand(
                "",
                "Descrição",
                new BigDecimal("100.00")
        );

        // Act & Assert
        assertThatThrownBy(() -> createService.execute(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service name cannot be empty");

        verifyNoInteractions(gateway);
    }
}
