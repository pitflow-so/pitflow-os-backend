package br.com.pitflow.operation.application;

import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.operation.infrastructure.api.dto.OrderDurationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GetServiceOrderDurationImpTest {

    private ServiceOrderRepository repository;
    private GetServiceOrderDurationImp useCase;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceOrderRepository.class);
        useCase = new GetServiceOrderDurationImp(repository);
    }

    @Test
    @DisplayName("Deve calcular a duração de uma OS já finalizada")
    void shouldCalculateDurationForFinishedOrder() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Reparo");

        LocalDateTime start = LocalDateTime.now().minusHours(2).minusMinutes(30);
        LocalDateTime end = LocalDateTime.now(); // 2h 30min later

        os.reconstituteExecutionStartedAt(start);
        os.reconstituteFinishedAt(end);
        os.reconstituteStatus(ServiceOrder.Status.FINISHED);

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act
        OrderDurationResponse response = useCase.execute(osId);

        // Assert
        assertThat(response.durationInMinutes()).isEqualTo(150.0); // 120 + 30
        assertThat(response.formattedDuration()).isEqualTo("2h 30min");
        assertThat(response.isStillRunning()).isFalse();
    }

    @Test
    @DisplayName("Should calculate duration for running order")
    void shouldCalculateDurationForRunningOrder() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Manutenção");

        // Started 45 minutes ago
        os.reconstituteExecutionStartedAt(LocalDateTime.now().minusMinutes(45));
        os.reconstituteStatus(ServiceOrder.Status.IN_EXECUTION);

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act
        OrderDurationResponse response = useCase.execute(osId);

        // Assert
        // Hours may vary slightly depending on execution time, so we check for a range
        assertThat(response.durationInMinutes()).isGreaterThanOrEqualTo(45.0);
        assertThat(response.formattedDuration()).contains("45min");
        assertThat(response.isStillRunning()).isTrue();
    }

    @Test
    @DisplayName("Should return not started when no execution date")
    void shouldReturnNotStartedWhenNoExecutionDate() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Aguardando");
        // Status RECEIVED ou IN_DIAGNOSIS não têm executionStartedAt

        when(repository.findById(osId)).thenReturn(Optional.of(os));

        // Act
        OrderDurationResponse response = useCase.execute(osId);

        // Assert
        assertThat(response.durationInMinutes()).isEqualTo(0.0);
        assertThat(response.formattedDuration()).isEqualTo("Não iniciada");
        assertThat(response.isStillRunning()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Arrange
        UUID osId = UUID.randomUUID();
        when(repository.findById(osId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(osId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service Order not found");
    }
}