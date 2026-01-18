package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.domain.Service;

import java.util.UUID;

public interface FindServiceById { Service execute(UUID id); }
