package br.com.pitflow.operation.infrastructure.web.dto;

public record ExternalDecisionRequest(
        String token,
        String reason
) {
}
