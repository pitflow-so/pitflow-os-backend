package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    private ServiceOrderMetricsGateway metricsGateway;
    private RegistryGateway registryGateway;

    @BeforeEach
    void setUp() {
        gateway = mock(ServiceOrderGateway.class);
        notificationGateway = mock(NotificationGateway.class);
        metricsGateway = mock(ServiceOrderMetricsGateway.class);
        tokenGateway = mock(TokenGateway.class);
        when(tokenGateway.generateToken(anyString(), anyMap()))
                .thenReturn("decision-token");
        registryGateway = mock(RegistryGateway.class);
        completeDiagnosis = new CompleteDiagnosisImp(
                gateway, notificationGateway, metricsGateway, tokenGateway, registryGateway, "dummyURL");
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

        var dummyEmail = "dummy@email.com";

        when(gateway.findById(osId)).thenReturn(Optional.of(os));
        when(registryGateway.findCustomerEmail(os.getCustomerId())).thenReturn(Optional.of(dummyEmail));

        // Act
        completeDiagnosis.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.AWAITING_APPROVAL);
        verify(gateway).save(os);
        verify(notificationGateway).send(eq(osId), any(Notification.class));
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

        var dummyEmail = "dummy@email.com";
        // Items not added

        when(gateway.findById(osId)).thenReturn(Optional.of(os));
        when(registryGateway.findCustomerEmail(os.getCustomerId())).thenReturn(Optional.of(dummyEmail));

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

        var dummyEmail = "dummy@email.com";

        when(gateway.findById(osId)).thenReturn(Optional.of(os));
        when(registryGateway.findCustomerEmail(os.getCustomerId())).thenReturn(Optional.of(dummyEmail));

        NotificationGateway notificationGateway = mock(NotificationGateway.class);
        var interactor = new CompleteDiagnosisImp(
                gateway, notificationGateway, metricsGateway, tokenGateway, registryGateway, "dummyURL");

        // Act
        interactor.execute(osId);

        // Assert
        assertThat(os.getStatus()).isEqualTo(ServiceOrder.Status.AWAITING_APPROVAL);
        verify(gateway).save(os);
        var notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationGateway).send(
                eq(os.getId()),
                notificationCaptor.capture()
        );
        assertThat(notificationCaptor.getValue().message())
                .contains("dummyURL/customer/budget?token=decision-token")
                .doesNotContain("/external/events/service-orders/decision");
    }
}
