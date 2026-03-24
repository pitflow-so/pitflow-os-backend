package br.com.pitflow.inventory.core.usecase.part.inputPort;

import br.com.pitflow.inventory.controller.dto.CreatePartCommand;
import br.com.pitflow.inventory.core.entity.Part;

public interface CreatePart {
    Part execute(CreatePartCommand dto);
}