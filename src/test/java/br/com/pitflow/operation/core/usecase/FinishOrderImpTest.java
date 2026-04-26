package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinishOrderImpTest {

    private NotificationGateway notificationGateway;
    private ServiceOrderGateway gateway;
    private FinishOrderImp finishOrder;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        notificationGateway = mock(NotificationGateway.class);
        finishOrder = new FinishOrderImp(notificationGateway, gateway);
    }

    @Test
    @DisplayName("Should finish order and record end timestamp successfully")
    void shouldFinishOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Revisão geral");
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Troca de freios", new BigDecimal("300.0"));
        os.completeDiagnosis();
        os.approve();

        var dummyEmail = "dummy@email.com";

        when(gateway.findById(osId)).thenReturn(Optional.of(os));
        when(gateway.findEmail(osId)).thenReturn(Optional.of(dummyEmail));

        // Act
        finishOrder.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.FINISHED);
        assertThat(os.getFinishedAt()).isNotNull();
        verify(gateway).save(os);
        verify(notificationGateway).send(eq(os.getId()), any(Notification.class));
    }

    @Test
    @DisplayName("Should fail to finish if order is not in IN_EXECUTION state")
    void shouldFailIfInvalidState() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de filtros");
        var dummyEmail = "dummy@email.com";

        when(gateway.findById(osId)).thenReturn(Optional.of(os));
        when(gateway.findEmail(osId)).thenReturn(Optional.of(dummyEmail));

        // Act & Assert
        assertThatThrownBy(() -> finishOrder.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order must be IN_EXECUTION to be finished.");
    }
}