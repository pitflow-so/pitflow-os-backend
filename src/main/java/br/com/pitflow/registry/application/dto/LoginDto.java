package br.com.pitflow.registry.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para login de usuário")
public record LoginDto(
        @Schema(description = "Username do mecânico")
        String username,

        @Schema(description = "Senha de acesso do mecânico")
        String password
) {
}
