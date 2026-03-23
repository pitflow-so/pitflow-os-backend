package br.com.pitflow.registry.core.usecase.mechanic.inputPort;

import br.com.pitflow.registry.core.usecase.mechanic.outputData.AuthenticationResult;
import br.com.pitflow.registry.controller.dto.LoginCommand;

public interface AuthenticateMechanic {
    AuthenticationResult execute(LoginCommand dto);
}
