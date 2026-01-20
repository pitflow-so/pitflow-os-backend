package br.com.pitflow.registry.application.dto;

import br.com.pitflow.registry.domain.Mechanic;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta da autenticação")
public record AuthenticationResult(
        @Schema(description = "Dados do mecânico autenticado")
        Mechanic mechanic,

        @Schema(description = "Token JWT para autenticação nas próximas requisições")
        String token
) {
}
