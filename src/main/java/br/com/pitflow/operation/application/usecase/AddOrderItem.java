package br.com.pitflow.operation.application.usecase;

import br.com.pitflow.operation.application.dto.AddOrderItemDto;

public interface AddOrderItem {
    void execute(AddOrderItemDto dto);
}
