package br.com.pitflow.registry.core.gateway;

public interface PasswordEncoderGateway {
    String encode(String rawPassword);
}
