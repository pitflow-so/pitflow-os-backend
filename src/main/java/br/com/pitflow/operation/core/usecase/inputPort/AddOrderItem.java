package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;

public interface AddOrderItem {
    void execute(AddOrderItemCommand dto);
}
