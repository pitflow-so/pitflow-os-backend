package br.com.pitflow.registry.controller.dto;

public record LoginCommand(
        String username,
        String password
) {
}
