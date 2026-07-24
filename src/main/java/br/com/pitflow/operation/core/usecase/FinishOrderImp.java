package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import br.com.pitflow.operation.core.valueobject.Email;
import br.com.pitflow.operation.core.usecase.inputPort.FinishOrder;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class FinishOrderImp implements FinishOrder {
    private final NotificationGateway notificationGateway;
    private final ServiceOrderGateway repository;
    private final ServiceOrderMetricsGateway metricsGateway;
    private final RegistryGateway registryGateway;

    public FinishOrderImp(
            NotificationGateway notificationGateway,
            ServiceOrderGateway repository,
            ServiceOrderMetricsGateway metricsGateway,
            RegistryGateway registryGateway
    ) {
        this.notificationGateway = notificationGateway;
        this.repository = repository;
        this.metricsGateway = metricsGateway;
        this.registryGateway = registryGateway;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));
        var emailAddress = registryGateway.findCustomerEmail(os.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Email address not found with OS ID: " + orderId));

        String previousStatus = os.getStatus().name();
        // Calcula quanto tempo a OS demorou para terminar
        Duration timeSpentInExecution = Duration.between(os.getExecutionStartedAt(), LocalDateTime.now());

        os.finish();
        repository.save(os);

        // Dispara a métrica de tempo para o Datadog!
        metricsGateway.recordTimeInStatus(previousStatus, timeSpentInExecution);

        String message = String.format("Sua Ordem de Serviço %s foi finalizada.",
                os.getId());

        var email = new Email(emailAddress);
        var notification = new Notification(message, email);
        notificationGateway.send(os.getId(), notification);
    }
}
