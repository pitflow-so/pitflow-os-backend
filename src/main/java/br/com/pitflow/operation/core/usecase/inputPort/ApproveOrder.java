package br.com.pitflow.operation.core.usecase.inputPort;


import java.util.UUID;

public interface ApproveOrder {
    void execute(UUID orderId);
}
