package br.com.pitflow.inventory.core.usecase.service.inputPort;

import br.com.pitflow.inventory.application.dto.CreateServiceDto;
import br.com.pitflow.inventory.core.entity.Service;

public interface CreateService {
    Service execute(CreateServiceDto dto);
}
