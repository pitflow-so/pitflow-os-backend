package br.com.pitflow.operation.core.gateway;

import java.time.Duration;

public interface ServiceOrderMetricsGateway {
    void incrementOrderCreated();
    void recordTimeInStatus(String statusName, Duration duration);
}
