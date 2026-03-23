package br.com.pitflow.registry.core.usecase.mechanic;

import br.com.pitflow.registry.core.entity.Mechanic;

public record AuthenticationResult(
        Mechanic mechanic,

        String token
) {
}
