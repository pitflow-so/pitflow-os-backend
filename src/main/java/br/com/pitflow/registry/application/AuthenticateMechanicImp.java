package br.com.pitflow.registry.application;

import br.com.pitflow.common.infrastructure.security.JwtService;
import br.com.pitflow.registry.application.dto.LoginDto;
import br.com.pitflow.registry.application.usecases.AuthenticateMechanic;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import br.com.pitflow.registry.application.dto.AuthenticationResult;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticateMechanicImp implements AuthenticateMechanic {

    private final MechanicRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticateMechanicImp(MechanicRepository repository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthenticationResult execute(LoginDto dto) {
        var mechanic = repository.findByUsername(dto.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.password(), mechanic.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(mechanic);

        return new AuthenticationResult(mechanic, token);
    }
}