package br.com.pitflow.operation.application.usecase;


import br.com.pitflow.operation.application.dto.CancelOrderDto;

public interface CancelOrder {
    void execute(CancelOrderDto dto);
}
