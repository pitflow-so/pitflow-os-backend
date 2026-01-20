package br.com.pitflow.registry.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta final de autenticação com token")
public record AuthenticationResponse(
        @Schema(description = "Token JWT gerado")
        String token,

        @Schema(description = "Dados resumidos do mecânico")
        MechanicResponse mechanic
) {
}