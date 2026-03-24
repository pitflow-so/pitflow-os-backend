package br.com.pitflow.registry.infrastructure.security;

import br.com.pitflow.common.core.gateway.PasswordVerifierGateway;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptPasswordVerifierAdapter implements PasswordVerifierGateway {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordVerifierAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}