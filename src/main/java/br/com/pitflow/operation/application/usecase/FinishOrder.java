package br.com.pitflow.operation.application.usecase;

import java.util.UUID;

public interface FinishOrder {
    void execute(UUID orderId);
}
