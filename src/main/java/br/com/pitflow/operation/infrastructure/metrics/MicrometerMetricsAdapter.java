package br.com.pitflow.operation.infrastructure.metrics;

import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.Duration;

public class MicrometerMetricsAdapter implements ServiceOrderMetricsGateway {
    private final MeterRegistry meterRegistry;

    public MicrometerMetricsAdapter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void incrementOrderCreated() {
        meterRegistry.counter("os.volume").increment();
    }

    @Override
    public void recordTimeInStatus(String statusName, Duration duration) {
        // Cria um timer chamado "os.execution.time" agrupado pela tag "status"
        meterRegistry.timer("os.execution.time", "status", statusName).record(duration);
    }
}
