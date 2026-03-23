package br.com.pitflow.operation.application;

import br.com.pitflow.operation.controller.dto.CancelOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.CancelOrderImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CancelOrderImpTest {

    private ServiceOrderGateway gateway;
    private CancelOrderImp cancelOrder;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        cancelOrder = new CancelOrderImp(gateway);
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void shouldCancelOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        // Usando o novo construtor com descrição que você criou
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de óleo");

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act
        cancelOrder.execute(new CancelOrderCommand(osId, "Dummy reason"));

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.CANCELLED);
        verify(gateway).save(os);
    }

    @Test
    @DisplayName("Should fail to cancel if order is already delivered")
    void shouldFailIfAlreadyDelivered() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Revisão");

        // Simulando o ciclo até a entrega para testar a trava de segurança [cite: 31]
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Limpeza de tanque", new java.math.BigDecimal("500.00"));
        os.completeDiagnosis();
        os.approve();
        os.finish(); // [cite: 30]
        os.deliver(); // [cite: 31]

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> cancelOrder.execute(new CancelOrderCommand(osId, "Dummy reason")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel an order that is already finished or delivered.");
    }

    @Test
    @DisplayName("Should cancel order with reason successfully")
    void shouldCancelOrderWithReasonSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        String reason = "Cliente achou o valor das peças muito alto";
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de amortecedores");

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act
        cancelOrder.execute(new CancelOrderCommand(osId, reason));

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.CANCELLED);
        assertThat(os.getCancelledAt()).isNotNull();
        // Aqui verificaríamos se o atributo foi salvo (se houver getter para cancellationDescription)
        verify(gateway).save(os);
    }
}
