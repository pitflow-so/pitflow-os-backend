package br.com.pitflow.registry.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "DTO para criar um mecânico")
public record CreateMechanicDto(
        @Schema(description = "Nome completo do mecânico", example = "João Silva")
        String name,

        @Schema(description = "Username do mecânico", example = "joaosilva")
        String username,

        @Schema(description = "Senha forte")
        String password
) {
}
