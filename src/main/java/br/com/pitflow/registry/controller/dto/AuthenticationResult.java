package br.com.pitflow.registry.controller.dto;

import br.com.pitflow.registry.core.entity.Mechanic;

public record AuthenticationResult(
        Mechanic mechanic,

        String token
) {
}
