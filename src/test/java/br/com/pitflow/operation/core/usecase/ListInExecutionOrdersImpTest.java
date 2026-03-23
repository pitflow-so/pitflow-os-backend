package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListInExecutionOrdersImpTest {

    private ServiceOrderGateway gateway;
    private ListInExecutionOrdersImp listInExecutionOrders;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        listInExecutionOrders = new ListInExecutionOrdersImp(gateway);
    }

    @Test
    @DisplayName("Should return orders in execution")
    void shouldReturnOrdersInExecution() {
        // Arrange
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Reparo");
        os.reconstituteStatus(ServiceOrder.Status.IN_EXECUTION);

        when(gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION))
                .thenReturn(List.of(os));

        // Act
        var result = listInExecutionOrders.execute();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ServiceOrder.Status.IN_EXECUTION);

        // Verify
        verify(gateway).findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION);
    }
}