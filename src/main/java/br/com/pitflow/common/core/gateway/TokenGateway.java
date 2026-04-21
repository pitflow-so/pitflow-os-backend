package br.com.pitflow.common.core.gateway;

import java.util.Map;

public interface TokenGateway {
    String generateToken(String subject, Map<String, Object> claims);
    String validateToken(String token);
    Map<String, Object> getClaims(String token);
}