package br.com.pitflow.operation.controller;

import br.com.pitflow.common.core.exception.InvalidTokenException;
import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.operation.controller.dto.ExternalDecisionCommand;
import br.com.pitflow.operation.core.enums.ExternalStatusEvent;

import java.util.Map;
import java.util.UUID;

public class ExternalDecisionController {
    private static final String EXTERNAL_DECISION_SUBJECT = "external-decision";

    private final TokenGateway tokenGateway;
    private final ServiceOrderController serviceOrderController;

    public ExternalDecisionController(
            TokenGateway tokenGateway,
            ServiceOrderController serviceOrderController
    ) {
        this.tokenGateway = tokenGateway;
        this.serviceOrderController = serviceOrderController;
    }

    public void processDecision(String token, String suppliedReason) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token must not be null or empty");
        }
        var claims = tokenGateway.getClaims(token);
        validateSubject(claims);

        var serviceOrderId = getServiceOrderIdFromClaim(claims);
        var event = getStatusFromClaim(claims);

        String reason = suppliedReason;
        if (reason == null || reason.isBlank()) {
            var reasonValue = claims.get("reason");
            reason = reasonValue instanceof String r ? r : null;
        }

        validateReasonForEvent(reason, event);

        var command = new ExternalDecisionCommand(serviceOrderId, event, reason);
        serviceOrderController.processExternalDecision(command);
    }

    private void validateSubject(Map<String, Object> claims) {
        if (!EXTERNAL_DECISION_SUBJECT.equals(claims.get("sub"))) {
            throw new InvalidTokenException("Invalid token subject");
        }
    }

    private UUID getServiceOrderIdFromClaim(Map<String, Object> claims){
        var value = claims.get("serviceOrderId");

        if (!(value instanceof String id)) {
            throw new InvalidTokenException("serviceOrderId must be a string");
        }

        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid serviceOrderId in token", e);
        }
    }

    private ExternalStatusEvent getStatusFromClaim(Map<String, Object> claims){
        var value = claims.get("status");

        if (!(value instanceof String status)) {
            throw new InvalidTokenException("status must be a string");
        }

        try {
            return ExternalStatusEvent.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid status in token: " + status, e);
        }
    }

    private void validateReasonForEvent(String reason, ExternalStatusEvent event){
        if (event == ExternalStatusEvent.REJECTED && (reason == null || reason.isBlank())) {
            throw new InvalidTokenException("Reason is required for REJECTED status");
        }
    }
}
