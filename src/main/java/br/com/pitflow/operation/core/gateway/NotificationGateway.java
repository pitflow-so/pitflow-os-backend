package br.com.pitflow.operation.core.gateway;

import br.com.pitflow.operation.core.gateway.dto.Notification;

import java.util.UUID;

public interface NotificationGateway {
    void send(UUID serviceOrderId, Notification notification);
}
