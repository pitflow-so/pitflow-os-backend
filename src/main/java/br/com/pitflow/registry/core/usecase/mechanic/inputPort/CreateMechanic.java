package br.com.pitflow.registry.core.usecase.mechanic.inputPort;

import br.com.pitflow.registry.controller.dto.CreateMechanicCommand;
import br.com.pitflow.registry.core.entity.Mechanic;

public interface CreateMechanic {
    Mechanic execute(CreateMechanicCommand dto);
}
