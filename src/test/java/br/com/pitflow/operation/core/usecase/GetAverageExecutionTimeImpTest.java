package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.outputData.ExecutionTimeMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAverageExecutionTimeImpTest {

    private ServiceOrderGateway gateway;
    private GetAverageExecutionTimeImp useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        useCase = new GetAverageExecutionTimeImp(gateway);
    }

    @Test
    @DisplayName("Deve retornar métrica zerada quando o repositório retornar null")
    void shouldReturnZeroWhenRepositoryReturnsNull() {
        // Arrange
        when(gateway.getAverageExecutionTimeInSeconds()).thenReturn(null);

        // Act
        ExecutionTimeMetrics metrics = useCase.execute();

        // Assert
        assertThat(metrics.averageMinutes()).isEqualTo(0.0);
        assertThat(metrics.formatted()).isEqualTo("0min");
    }

    @Test
    @DisplayName("Deve retornar métrica zerada quando o repositório retornar zero")
    void shouldReturnZeroWhenRepositoryReturnsZero() {
        // Arrange
        when(gateway.getAverageExecutionTimeInSeconds()).thenReturn(0.0);

        // Act
        ExecutionTimeMetrics metrics = useCase.execute();

        // Assert
        assertThat(metrics.averageMinutes()).isEqualTo(0.0);
        assertThat(metrics.formatted()).isEqualTo("0min");
    }

    @ParameterizedTest
    @DisplayName("Should format average time correctly for various inputs")
    @CsvSource({
            "1500.0, 25.0, 25min",      // 25 minutos (apenas minutos)
            "3600.0, 60.0, 1h 0min",    // 60 minutos (1h exata)
            "4500.0, 75.0, 1h 15min",   // 75 minutos (horas e minutos)
            "7500.0, 125.0, 2h 5min"    // 125 minutos (mais de 2 horas)
    })
    void shouldFormatAverageTimeCorrectly(Double seconds, Double expectedMinutes, String expectedFormatted) {
        // Arrange
        when(gateway.getAverageExecutionTimeInSeconds()).thenReturn(seconds);

        // Act
        ExecutionTimeMetrics metrics = useCase.execute();

        // Assert
        assertThat(metrics.averageMinutes()).isEqualTo(expectedMinutes);
        assertThat(metrics.formatted()).isEqualTo(expectedFormatted);
    }
}