package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.application.dto.CreateMechanicDto;
import br.com.pitflow.registry.domain.Mechanic;

public interface CreateMechanic {
    Mechanic execute(CreateMechanicDto dto);
}
