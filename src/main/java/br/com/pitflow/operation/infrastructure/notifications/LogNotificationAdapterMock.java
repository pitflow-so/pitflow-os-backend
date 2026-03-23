package br.com.pitflow.operation.infrastructure.notifications;

import br.com.pitflow.operation.core.gateway.NotificationGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LogNotificationAdapterMock implements NotificationGateway {
    private static final Logger logger = LoggerFactory.getLogger(LogNotificationAdapterMock.class);

    public LogNotificationAdapterMock() {
    }
    @Override
    public void send(UUID serviceOrderId, String message) {
        logger.info("[NOTIFICATION] - OS ID: {} - Message: {}", serviceOrderId, message);
    }
}
