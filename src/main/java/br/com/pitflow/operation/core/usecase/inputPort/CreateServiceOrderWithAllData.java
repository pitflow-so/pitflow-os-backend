package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.controller.dto.CreateServiceOrderAllDataCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;

public interface CreateServiceOrderWithAllData {
    ServiceOrder execute(CreateServiceOrderAllDataCommand command);
}
