package br.com.pitflow.registry.presenter.dto;

public record AuthenticationResponse(
        String token,
        MechanicResponse mechanic
) {
}