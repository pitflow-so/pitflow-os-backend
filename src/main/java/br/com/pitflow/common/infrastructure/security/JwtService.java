package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.registry.domain.Mechanic;

public interface JwtService {
    String generateToken(Mechanic mechanic);
    String validateToken(String token);
}
