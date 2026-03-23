package br.com.pitflow.operation.controller.dto;

import java.util.UUID;

public record CancelOrderCommand(
        UUID serviceOrderId,
        String reason
) {}