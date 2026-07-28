package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderReadyForExecution;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MarkServiceOrderReadyForExecutionImpTest {
    private final ServiceOrderGateway orders = mock(ServiceOrderGateway.class);
    private final OperationEventGateway events = mock(OperationEventGateway.class);
    private final TransactionGateway tx = new TransactionGateway() {
        public <T> T execute(Supplier<T> operation) { return operation.get(); }
        public void execute(Runnable operation) { operation.run(); }
    };
    private final MarkServiceOrderReadyForExecutionImp useCase =
            new MarkServiceOrderReadyForExecutionImp(orders, events, tx, Clock.systemUTC());

    @Test
    void updatesPublishesAndHandlesReplay() {
        var command = command();
        var order = order(ServiceOrder.Status.AWAITING_PAYMENT);
        when(orders.findById(command.serviceOrderId())).thenReturn(Optional.of(order));
        assertEquals(MarkServiceOrderReadyForExecution.Result.UPDATED, useCase.execute(command));
        assertEquals(ServiceOrder.Status.READY_FOR_EXECUTION, order.getStatus());
        verify(orders).save(order);
        verify(events).saveReadyForExecution(any());

        reset(orders, events);
        when(orders.findById(command.serviceOrderId())).thenReturn(Optional.of(order));
        assertEquals(MarkServiceOrderReadyForExecution.Result.ALREADY_PROCESSED, useCase.execute(command));
        verify(orders, never()).save(any());
    }

    @Test
    void rejectsInvalidAndMissingOrder() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
        var command = command();
        when(orders.findById(command.serviceOrderId())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
    }

    private MarkServiceOrderReadyForExecution.Command command() {
        return new MarkServiceOrderReadyForExecution.Command(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "external-1",
                Instant.now());
    }
    private ServiceOrder order(ServiceOrder.Status status) {
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "OS");
        order.reconstituteStatus(status);
        return order;
    }
}
