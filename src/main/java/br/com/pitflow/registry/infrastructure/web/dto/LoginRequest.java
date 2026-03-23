package br.com.pitflow.registry.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para login de usuário")
public record LoginRequest(
        @Schema(description = "Username do mecânico")
        String username,

        @Schema(description = "Senha de acesso do mecânico")
        String password
) {
}
