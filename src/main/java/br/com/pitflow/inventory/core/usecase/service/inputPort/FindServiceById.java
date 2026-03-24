package br.com.pitflow.inventory.core.usecase.service.inputPort;

import br.com.pitflow.inventory.core.entity.Service;

import java.util.UUID;

public interface FindServiceById { Service execute(UUID id); }
