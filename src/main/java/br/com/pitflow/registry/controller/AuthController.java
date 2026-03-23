package br.com.pitflow.registry.controller;

import br.com.pitflow.registry.controller.dto.LoginCommand;
import br.com.pitflow.registry.infrastructure.web.dto.LoginRequest;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.AuthenticateMechanic;
import br.com.pitflow.registry.presenter.MechanicPresenter;
import br.com.pitflow.registry.presenter.dto.AuthenticationResponse;

public class AuthController {
    private final AuthenticateMechanic authenticateMechanic;

    public AuthController(AuthenticateMechanic authenticateMechanic) {
        this.authenticateMechanic = authenticateMechanic;
    }

    public AuthenticationResponse authenticate(LoginRequest dto) {
        var command = new LoginCommand(dto.username(), dto.password());
        var authentication = authenticateMechanic.execute(command);

        return new AuthenticationResponse(
                authentication.token(),
                MechanicPresenter.toResponse(authentication.mechanic())
        );
    }
}
