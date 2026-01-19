package br.com.pitflow.operation.application;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ListInExecutionOrdersImpTest {

    private ServiceOrderRepository repository;
    private ListInExecutionOrdersImp listInExecutionOrders;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        listInExecutionOrders = new ListInExecutionOrdersImp(repository);
    }

    @Test
    @DisplayName("Should return orders in execution")
    void shouldReturnOrdersInExecution() {
        // Arrange
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Reparo");
        os.reconstituteStatus(ServiceOrder.Status.IN_EXECUTION);

        when(repository.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION))
                .thenReturn(List.of(os));

        // Act
        var result = listInExecutionOrders.execute();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ServiceOrder.Status.IN_EXECUTION);

        // Verify
        verify(repository).findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION);
    }
}