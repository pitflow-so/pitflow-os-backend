package br.com.pitflow.operation.core.usecase.outputData;

public record ExecutionTimeMetrics(
    Double averageMinutes,
    String formatted
){
}
