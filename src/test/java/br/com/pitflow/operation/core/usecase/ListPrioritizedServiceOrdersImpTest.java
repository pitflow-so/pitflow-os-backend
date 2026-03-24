package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListPrioritizedServiceOrdersImpTest {
    private ServiceOrderGateway gateway;
    private ListPrioritizedServiceOrdersImp useCase;

    @BeforeEach
    void setup() {
        gateway = mock(ServiceOrderGateway.class);
        useCase = new ListPrioritizedServiceOrdersImp(gateway);
    }

    @Test
    void shouldReturnOrdersInCorrectPriorityOrder() {
        // Arrange
        var order1 = mock(ServiceOrder.class);
        var order2 = mock(ServiceOrder.class);
        var order3 = mock(ServiceOrder.class);
        var order4 = mock(ServiceOrder.class);

        when(gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION))
                .thenReturn(List.of(order1));

        when(gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.AWAITING_APPROVAL))
                .thenReturn(List.of(order2));

        when(gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_DIAGNOSIS))
                .thenReturn(List.of(order3));

        when(gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.RECEIVED))
                .thenReturn(List.of(order4));

        // Act
        var result = useCase.execute();

        // Assert
        assertEquals(List.of(order1, order2, order3, order4), result);

        // Verify
        verify(gateway).findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION);
        verify(gateway).findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.AWAITING_APPROVAL);
        verify(gateway).findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_DIAGNOSIS);
        verify(gateway).findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.RECEIVED);
    }

    @Test
    void shouldNotCallFinishedOrDeliveredStatuses() {
        // Arrange
        when(gateway.findByStatusOrderByCreatedAtAsc(any()))
                .thenReturn(List.of());

        // Act
        useCase.execute();

        // Assert
        verify(gateway, never())
                .findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.FINISHED);

        verify(gateway, never())
                .findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.DELIVERED);
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersExist() {
        // Arrange
        when(gateway.findByStatusOrderByCreatedAtAsc(any()))
                .thenReturn(List.of());

        // Act
        var result = useCase.execute();

        // Assert
        assertEquals(List.of(), result);

        // Verify
        verify(gateway, times(4))
                .findByStatusOrderByCreatedAtAsc(any());
    }
}
