package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartExecutionImpTest {

    private final ServiceOrderGateway repository = mock(ServiceOrderGateway.class);
    private final StartExecutionImp useCase = new StartExecutionImp(repository);

    @Test
    void startsExecutionWhenOrderIsReady() {
        var orderId = UUID.randomUUID();
        var order = readyOrder();
        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        useCase.execute(orderId);

        assertThat(order.getStatus()).isEqualTo(ServiceOrder.Status.IN_EXECUTION);
        assertThat(order.getExecutionStartedAt()).isNotNull();
        verify(repository).save(order);
    }

    @Test
    void rejectsOrderThatIsNotReadyForExecution() {
        var orderId = UUID.randomUUID();
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Revisão");
        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> useCase.execute(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Order must be READY_FOR_EXECUTION to start execution.");
        verify(repository, never()).save(order);
    }

    @Test
    void rejectsUnknownOrder() {
        var orderId = UUID.randomUUID();
        when(repository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service Order not found with ID: " + orderId);
    }

    private ServiceOrder readyOrder() {
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Revisão");
        order.startDiagnosis();
        order.addService(UUID.randomUUID(), "Alinhamento", java.math.BigDecimal.TEN);
        order.completeDiagnosis();
        order.approve();
        order.markAwaitingPayment();
        order.markReadyForExecution();
        return order;
    }
}
