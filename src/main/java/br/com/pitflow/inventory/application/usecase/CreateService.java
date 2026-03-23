package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.application.dto.CreateServiceDto;
import br.com.pitflow.inventory.core.entity.Service;

public interface CreateService {
    Service execute(CreateServiceDto dto);
}
