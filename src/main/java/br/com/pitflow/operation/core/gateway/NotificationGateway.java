package br.com.pitflow.operation.core.gateway;

import java.util.UUID;

public interface NotificationGateway {
    void send(UUID serviceOrderId, String message);
}
