package br.com.pitflow.operation.application.usecase;

import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
import br.com.pitflow.operation.domain.ServiceOrder;

public interface CreateServiceOrder {
    ServiceOrder execute(CreateServiceOrderDto dto);
}
