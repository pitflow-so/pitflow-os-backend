package br.com.pitflow.operation.application.usecase;

import java.util.UUID;

public interface NotificationService {
    void send(UUID serviceOrderId, String message);
}
