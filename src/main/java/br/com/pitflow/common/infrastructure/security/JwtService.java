package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.registry.core.entity.Mechanic;

public interface JwtService {
    String generateToken(Mechanic mechanic);
    String validateToken(String token);
}
