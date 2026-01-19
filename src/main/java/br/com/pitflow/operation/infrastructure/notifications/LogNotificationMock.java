package br.com.pitflow.operation.infrastructure.notifications;

import br.com.pitflow.operation.application.usecase.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LogNotificationMock implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(LogNotificationMock.class);

    public LogNotificationMock() {
    }
    @Override
    public void send(UUID serviceOrderId, String message) {
        logger.info("[NOTIFICATION] - OS ID: {} - Message: {}", serviceOrderId, message);
    }
}
