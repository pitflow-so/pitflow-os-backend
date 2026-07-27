package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApproveOrderImpTest {

    private ServiceOrderGateway gateway;
    private OperationEventGateway eventGateway;
    private ApproveOrderImp approveOrder;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        eventGateway = mock(OperationEventGateway.class);
        TransactionGateway transactionGateway =
                mock(TransactionGateway.class);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(transactionGateway).execute(any(Runnable.class));
        approveOrder = new ApproveOrderImp(
                gateway,
                eventGateway,
                transactionGateway
        );
    }

    @Test
    @DisplayName("Should approve budget and register outbox event")
    void shouldApproveOrderSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Barulho na suspensão");
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Reparo", new BigDecimal("150.0"));
        os.completeDiagnosis(); // Agora está AWAITING_APPROVAL

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act
        approveOrder.execute(osId);

        // Assert
        assertThat(os.getStatus())
                .isEqualTo(ServiceOrder.Status.PAYMENT_PROCESSING);
        assertThat(os.getExecutionStartedAt()).isNull();
        verify(gateway).save(os);
        verify(eventGateway).saveBudgetApproved(
                any(ServiceOrderBudgetApproved.class)
        );
    }

    @Test
    @DisplayName("Should fail to approve if order is not in AWAITING_APPROVAL state")
    void shouldFailIfInvalidState() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de óleo");

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> approveOrder.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order must be AWAITING_APPROVAL to be approved.");
        verify(eventGateway, never()).saveBudgetApproved(any());
    }
}
