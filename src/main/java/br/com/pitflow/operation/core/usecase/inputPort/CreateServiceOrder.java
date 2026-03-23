package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;

public interface CreateServiceOrder {
    ServiceOrder execute(CreateServiceOrderCommand dto);
}
