package br.com.pitflow.inventory.core.usecase.service.inputPort;

import br.com.pitflow.inventory.controller.dto.UpdateServiceCommand;

import java.util.UUID;

public interface UpdateService {
    void execute(UUID id, UpdateServiceCommand dto);
}
