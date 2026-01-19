package br.com.pitflow.operation.application.usecase;

import java.util.UUID;

public interface DeliverOrder {
    void execute(UUID orderId);
}
