package br.com.pitflow.common.core.gateway;

public interface PasswordVerifierGateway {
    boolean matches(String rawPassword, String encodedPassword);
}