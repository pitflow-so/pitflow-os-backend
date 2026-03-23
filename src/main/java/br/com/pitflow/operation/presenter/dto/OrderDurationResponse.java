package br.com.pitflow.operation.presenter.dto;

import java.util.UUID;

public record OrderDurationResponse(
        UUID serviceOrderId,
        Double durationInMinutes,
        String formattedDuration,
        boolean isStillRunning
) {}
