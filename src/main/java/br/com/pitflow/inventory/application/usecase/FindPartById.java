package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.core.entity.Part;

import java.util.UUID;

public interface FindPartById { Part execute(UUID id); }