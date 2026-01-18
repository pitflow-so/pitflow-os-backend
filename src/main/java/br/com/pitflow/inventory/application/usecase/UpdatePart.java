package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.application.dto.UpdatePartDto;

import java.util.UUID;

public interface UpdatePart { void execute(UUID id, UpdatePartDto dto); }