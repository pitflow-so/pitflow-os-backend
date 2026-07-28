package br.com.pitflow.common.infrastructure.notifications;

import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LogNotificationAdapterMock implements NotificationGateway {
    private static final Logger logger = LoggerFactory.getLogger(LogNotificationAdapterMock.class);

    @Override
    public void send(UUID serviceOrderId, Notification notification) {
        logger.info("[NOTIFICATION] - OS ID: {} - Message: {}", serviceOrderId, notification.message());
    }
}
