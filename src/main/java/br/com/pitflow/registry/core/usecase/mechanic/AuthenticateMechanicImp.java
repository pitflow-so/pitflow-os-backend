package br.com.pitflow.registry.core.usecase.mechanic;

import br.com.pitflow.common.core.gateway.PasswordVerifierGateway;
import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.registry.controller.dto.LoginCommand;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.AuthenticateMechanic;
import br.com.pitflow.registry.core.usecase.mechanic.outputData.AuthenticationResult;

import java.util.Map;

public class AuthenticateMechanicImp implements AuthenticateMechanic {

    private final MechanicGateway mechanicGateway;
    private final PasswordVerifierGateway passwordVerifier;
    private final TokenGateway tokenGateway;

    public AuthenticateMechanicImp(
            MechanicGateway mechanicGateway,
            PasswordVerifierGateway passwordVerifier,
            TokenGateway tokenGateway) {
        this.mechanicGateway = mechanicGateway;
        this.passwordVerifier = passwordVerifier;
        this.tokenGateway = tokenGateway;
    }

    @Override
    public AuthenticationResult execute(LoginCommand command) {
        var mechanic = mechanicGateway.findByUsername(command.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordVerifier.matches(command.password(), mechanic.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        var claims = Map.<String, Object>of(
                "name", mechanic.getName(),
                "role", mechanic.getRole()
        );
        var token = tokenGateway.generateToken(mechanic.getUsername(), claims);

        return new AuthenticationResult(mechanic, token);
    }
}