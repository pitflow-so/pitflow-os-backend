package br.com.pitflow.operation.core.usecase.inputPort;


import br.com.pitflow.operation.controller.dto.CancelOrderCommand;

public interface CancelOrder {
    void execute(CancelOrderCommand dto);
}
