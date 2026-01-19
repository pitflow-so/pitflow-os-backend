package br.com.pitflow.operation.application.usecase;

import br.com.pitflow.operation.domain.ServiceOrder;

import java.util.UUID;

public interface GetServiceOrderById {
    ServiceOrder execute(UUID id);
}
