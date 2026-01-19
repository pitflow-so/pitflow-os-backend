package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.NotificationService;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinishOrderImpTest {

    private NotificationService notificationService;
    private ServiceOrderRepository repository;
    private FinishOrderImp finishOrder;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        notificationService = mock(NotificationService.class);
        finishOrder = new FinishOrderImp(notificationService, repository);
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

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act
        finishOrder.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.FINISHED);
        assertThat(os.getFinishedAt()).isNotNull();
        verify(repository).save(os);
        verify(notificationService).send(eq(os.getId()), anyString());
    }

    @Test
    @DisplayName("Should fail to finish if order is not in IN_EXECUTION state")
    void shouldFailIfInvalidState() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Troca de filtros");

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> finishOrder.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order must be IN_EXECUTION to be finished.");
    }
}