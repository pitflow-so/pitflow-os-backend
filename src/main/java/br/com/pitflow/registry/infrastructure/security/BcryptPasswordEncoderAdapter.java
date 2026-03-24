package br.com.pitflow.registry.infrastructure.security;

import br.com.pitflow.registry.core.gateway.PasswordEncoderGateway;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptPasswordEncoderAdapter implements PasswordEncoderGateway {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}