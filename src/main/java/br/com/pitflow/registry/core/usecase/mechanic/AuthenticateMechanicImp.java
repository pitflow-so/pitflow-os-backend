package br.com.pitflow.registry.core.usecase.mechanic;

import br.com.pitflow.common.infrastructure.security.JwtService;
import br.com.pitflow.registry.controller.dto.AuthenticationResult;
import br.com.pitflow.registry.controller.dto.LoginCommand;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.AuthenticateMechanic;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticateMechanicImp implements AuthenticateMechanic {

    private final MechanicGateway repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticateMechanicImp(MechanicGateway repository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthenticationResult execute(LoginCommand dto) {
        var mechanic = repository.findByUsername(dto.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.password(), mechanic.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(mechanic);

        return new AuthenticationResult(mechanic, token);
    }
}