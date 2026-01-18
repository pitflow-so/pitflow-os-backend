package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.application.dto.CreateServiceDto;
import br.com.pitflow.inventory.domain.Service;

public interface CreateService {
    Service execute(CreateServiceDto dto);
}
