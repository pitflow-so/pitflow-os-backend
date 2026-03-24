package br.com.pitflow.registry.core.usecase.mechanic.outputData;

import br.com.pitflow.registry.core.entity.Mechanic;

public record AuthenticationResult(
        Mechanic mechanic,

        String token
) {
}
