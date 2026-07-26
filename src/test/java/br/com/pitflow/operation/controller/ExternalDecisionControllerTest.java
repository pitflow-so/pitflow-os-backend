package br.com.pitflow.operation.controller;

import br.com.pitflow.common.core.exception.InvalidTokenException;
import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.operation.controller.dto.ExternalDecisionCommand;
import br.com.pitflow.operation.core.enums.ExternalStatusEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExternalDecisionControllerTest {

    private final TokenGateway tokenGateway = mock(TokenGateway.class);
    private final ServiceOrderController serviceOrderController =
            mock(ServiceOrderController.class);
    private final ExternalDecisionController controller =
            new ExternalDecisionController(
                    tokenGateway,
                    serviceOrderController
            );

    @Test
    void processesApprovedDecisionToken() {
        UUID orderId = UUID.randomUUID();
        givenDecisionToken(orderId, "APPROVED", "AWAITING_APPROVAL");

        controller.processDecision("token", null);

        ExternalDecisionCommand command = capturedCommand();
        assertThat(command.serviceOrderId()).isEqualTo(orderId);
        assertThat(command.event()).isEqualTo(ExternalStatusEvent.APPROVED);
        assertThat(command.reason()).isNull();
    }

    @Test
    void usesReasonProvidedByRejectionForm() {
        UUID orderId = UUID.randomUUID();
        givenDecisionToken(orderId, "REJECTED", "AWAITING_APPROVAL");

        controller.processDecision("token", "Valor acima do esperado");

        ExternalDecisionCommand command = capturedCommand();
        assertThat(command.event()).isEqualTo(ExternalStatusEvent.REJECTED);
        assertThat(command.reason()).isEqualTo("Valor acima do esperado");
    }

    @Test
    void rejectsRejectionWithoutReason() {
        when(tokenGateway.getClaims("token")).thenReturn(
                claims(UUID.randomUUID(), "REJECTED")
        );

        assertThatThrownBy(() -> controller.processDecision("token", null))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Reason is required");
    }

    @Test
    void rejectsTokenCreatedForAnotherPurpose() {
        Map<String, Object> claims = claims(
                UUID.randomUUID(),
                "APPROVED"
        );
        claims.put("sub", "customer");
        when(tokenGateway.getClaims("token")).thenReturn(claims);

        assertThatThrownBy(() -> controller.processDecision("token", null))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("subject");
    }

    @Test
    void acceptsReplayOfApprovedDecisionWithoutRepeatingEffect() {
        UUID orderId = UUID.randomUUID();
        givenDecisionToken(orderId, "APPROVED", "PAYMENT_PROCESSING");

        controller.processDecision("token", null);

        verify(serviceOrderController, never()).processExternalDecision(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void acceptsReplayOfRejectedDecisionWithoutRepeatingEffect() {
        UUID orderId = UUID.randomUUID();
        givenDecisionToken(orderId, "REJECTED", "CANCELLED");

        controller.processDecision("token", "Mesmo motivo");

        verify(serviceOrderController, never()).processExternalDecision(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void rejectsOppositeDecisionAfterApproval() {
        UUID orderId = UUID.randomUUID();
        givenDecisionToken(orderId, "REJECTED", "PAYMENT_PROCESSING");

        assertThatThrownBy(
                () -> controller.processDecision("token", "Mudei de ideia")
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already recorded");

        verify(serviceOrderController, never()).processExternalDecision(
                org.mockito.ArgumentMatchers.any()
        );
    }

    private Map<String, Object> claims(UUID orderId, String status) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "external-decision");
        claims.put("serviceOrderId", orderId.toString());
        claims.put("status", status);
        return claims;
    }

    private void givenDecisionToken(
            UUID orderId,
            String decision,
            String currentStatus
    ) {
        when(tokenGateway.getClaims("token"))
                .thenReturn(claims(orderId, decision));
        when(serviceOrderController.getServiceOrderStatus(orderId))
                .thenReturn(currentStatus);
    }

    private ExternalDecisionCommand capturedCommand() {
        ArgumentCaptor<ExternalDecisionCommand> captor =
                ArgumentCaptor.forClass(ExternalDecisionCommand.class);
        verify(serviceOrderController).processExternalDecision(captor.capture());
        return captor.getValue();
    }
}
