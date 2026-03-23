package br.com.pitflow.inventory.core.usecase.part.inputPort;

import br.com.pitflow.inventory.controller.dto.UpdatePartCommand;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdatePartRequest;

import java.util.UUID;

public interface UpdatePart { void execute(UUID id, UpdatePartCommand dto); }