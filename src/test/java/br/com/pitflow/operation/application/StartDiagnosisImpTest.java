package br.com.pitflow.operation.application;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.StartDiagnosisImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartDiagnosisImpTest {

    private ServiceOrderGateway gateway;
    private StartDiagnosisImp startDiagnosis;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        startDiagnosis = new StartDiagnosisImp(gateway);
    }

    @Test
    @DisplayName("Should start diagnosis and record timestamp successfully")
    void shouldStartDiagnosisSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Dummy problem");

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act
        startDiagnosis.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.IN_DIAGNOSIS);
        assertThat(os.getDiagnosisStartedAt()).isNotNull();
        verify(gateway).save(os);
    }

    @Test
    @DisplayName("Should fail if Service Order is not in RECEIVED status")
    void shouldFailIfInvalidStatus() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Dummy problem");
        os.cancel("Dummy reason");

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> startDiagnosis.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Order must be RECEIVED to start diagnosis.");
    }
}