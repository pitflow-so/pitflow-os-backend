package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompleteDiagnosisImpTest {

    private ServiceOrderGateway gateway;
    private NotificationGateway notificationGateway;
    private TokenGateway tokenGateway;
    private CompleteDiagnosisImp completeDiagnosis;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        notificationGateway = mock(NotificationGateway.class);
        tokenGateway = mock(TokenGateway.class);
        completeDiagnosis = new CompleteDiagnosisImp(gateway, notificationGateway, tokenGateway, "dummyURL");
    }

    @Test
    @DisplayName("Should complete diagnosis and move to awaiting approval successfully")
    void shouldCompleteDiagnosisSuccessfully() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "O carro está muito sujo");
        os.setId(osId);
        os.startDiagnosis(); // Muda para IN_DIAGNOSIS
        os.addService(UUID.randomUUID(), "Limpeza", new BigDecimal("100.0")); // Adiciona item obrigatório

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act
        completeDiagnosis.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.AWAITING_APPROVAL);
        verify(gateway).save(os);
        verify(notificationGateway).send(eq(osId), anyString());
    }

    @Test
    @DisplayName("Should fail to complete diagnosis if Service Order is not found")
    void shouldFailIfOsNotFound() {
        // Arrange
        UUID osId = UUID.randomUUID();
        when(gateway.findById(osId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> completeDiagnosis.execute(osId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service Order not found");
    }

    @Test
    @DisplayName("Should fail if domain rules are violated (e.g., no items added)")
    void shouldFailIfDomainRuleViolated() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Dummy");
        os.startDiagnosis();
        // Items not added

        when(gateway.findById(osId)).thenReturn(Optional.of(os));

        // Act & Assert
        assertThatThrownBy(() -> completeDiagnosis.execute(osId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot complete diagnosis without adding at least one item.");
    }

    @Test
    @DisplayName("Should complete diagnosis and send notification successfully")
    void shouldCompleteDiagnosisAndNotify() {
        // Arrange
        UUID osId = UUID.randomUUID();
        var os = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Teste");
        os.startDiagnosis();
        os.addService(UUID.randomUUID(), "Reparo", new BigDecimal("100"));

        when(gateway.findById(osId)).thenReturn(Optional.of(os));
        NotificationGateway notificationGateway = mock(NotificationGateway.class);
        var interactor = new CompleteDiagnosisImp(gateway, notificationGateway, tokenGateway, "dummyURL");

        // Act
        interactor.execute(osId);

        // Assert
        //TODO: fix erro from token
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.AWAITING_APPROVAL);
        verify(gateway).save(os);
        verify(notificationGateway).send(eq(os.getId()), anyString());
    }
}