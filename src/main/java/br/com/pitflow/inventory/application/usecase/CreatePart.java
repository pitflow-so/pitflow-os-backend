package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.application.dto.CreatePartDto;
import br.com.pitflow.inventory.core.entity.Part;

public interface CreatePart {
    Part execute(CreatePartDto dto);
}