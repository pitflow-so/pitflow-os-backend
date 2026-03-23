package br.com.pitflow.registry.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "DTO para criar um mecânico")
public record CreateMechanicRequest(
        @Schema(description = "Nome completo do mecânico", example = "João Silva")
        String name,

        @Schema(description = "Username do mecânico", example = "joaosilva")
        String username,

        @Schema(description = "Senha forte")
        String password
) {
}
