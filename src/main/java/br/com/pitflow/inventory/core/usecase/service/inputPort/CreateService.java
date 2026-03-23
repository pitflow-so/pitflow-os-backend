package br.com.pitflow.inventory.core.usecase.service.inputPort;

import br.com.pitflow.inventory.controller.dto.CreateServiceCommand;
import br.com.pitflow.inventory.core.entity.Service;

public interface CreateService {
    Service execute(CreateServiceCommand dto);
}
