package br.com.pitflow.registry.presenter.dto;

import java.util.UUID;

public record MechanicResponse(
        UUID id,

        String name,

        String username
) {
}
