package br.com.pitflow.operation.application.usecase;


import java.util.UUID;

public interface ApproveOrder {
    void execute(UUID orderId);
}
