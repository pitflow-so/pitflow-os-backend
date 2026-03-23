package br.com.pitflow.registry.controller;

import br.com.pitflow.registry.controller.dto.CreateMechanicCommand;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.CreateMechanic;
import br.com.pitflow.registry.infrastructure.web.dto.CreateMechanicRequest;
import br.com.pitflow.registry.presenter.MechanicPresenter;
import br.com.pitflow.registry.presenter.dto.MechanicResponse;

public class MechanicController {
    private final CreateMechanic createMechanic;

    public MechanicController(CreateMechanic createMechanic) {
        this.createMechanic = createMechanic;
    }

    public MechanicResponse create(CreateMechanicRequest dto) {
        var command = new CreateMechanicCommand(dto.name(), dto.username(), dto.password());
        var mechanic = createMechanic.execute(command);

        return MechanicPresenter.toResponse(mechanic);
    }
}
