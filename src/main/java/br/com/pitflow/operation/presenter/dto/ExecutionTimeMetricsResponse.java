package br.com.pitflow.operation.presenter.dto;

public record ExecutionTimeMetricsResponse(
        Double averageTimeInMinutes,

        String formattedTime
) {}