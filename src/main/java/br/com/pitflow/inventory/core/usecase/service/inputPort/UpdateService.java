package br.com.pitflow.inventory.core.usecase.service.inputPort;

import br.com.pitflow.inventory.application.dto.UpdateServiceDto;

import java.util.UUID;

public interface UpdateService {
    void execute(UUID id, UpdateServiceDto dto);
}
