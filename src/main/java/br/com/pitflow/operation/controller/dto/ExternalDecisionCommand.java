package br.com.pitflow.operation.controller.dto;

import br.com.pitflow.operation.core.enums.ExternalStatusEvent;

import java.util.UUID;

public record ExternalDecisionCommand(
        UUID serviceOrderId,
        ExternalStatusEvent event,
        String reason
) {}