package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.CancelOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
    @DisplayName("Should cancel order successfully and persist changes")
    void shouldCancelOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        String reason = "Cliente desistiu";

        var os = new ServiceOrder(osId, UUID.randomUUID(), "Troca de óleo");
        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act
        cancelOrder.execute(new CancelOrderCommand(osId, reason));

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.CANCELLED);
        assertThat(os.getCancelledAt()).isNotNull();
        assertThat(os.getCancellationDescription()).isEqualTo(reason);

        // Verify
        verify(gateway).save(os);
    }

    @Test
    @DisplayName("Should fail to cancel if order is already delivered")
    void shouldFailIfAlreadyDelivered() {
        // Arrange
        UUID osId = UUID.randomUUID();

        var os = new ServiceOrder(osId, UUID.randomUUID(), "Revisão completa");

        // Simula fluxo completo até entrega
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Limpeza de tanque", new BigDecimal("500.00"));
        os.completeDiagnosis();
        os.approve();
        os.markAwaitingPayment();
        os.markReadyForExecution();
        os.startExecution();
        os.finish();
        os.deliver();

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() ->
                cancelOrder.execute(new CancelOrderCommand(osId, "Motivo qualquer"))
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel an order that is already finished or delivered.");

        // Verify
        verify(gateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when reason is null")
    void shouldThrowExceptionWhenReasonIsNull() {
        // Arrange
        UUID osId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() ->
                cancelOrder.execute(new CancelOrderCommand(osId, null))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cancellation reason is required");

        // Verify
        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("Should throw exception when reason is blank")
    void shouldThrowExceptionWhenReasonIsBlank() {
        // Arrange
        UUID osId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() ->
                cancelOrder.execute(new CancelOrderCommand(osId, "   "))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cancellation reason is required");

        // Verify
        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("Should throw exception when service order is not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Arrange
        UUID osId = UUID.randomUUID();

        when(gateway.findById(osId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                cancelOrder.execute(new CancelOrderCommand(osId, "Motivo válido"))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service Order not found");

        // Verify
        verify(gateway, never()).save(any());
    }
}
