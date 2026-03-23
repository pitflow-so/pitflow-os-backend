package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.core.entity.ServiceOrder;

import java.util.UUID;

public interface GetServiceOrderById {
    ServiceOrder execute(UUID id);
}
