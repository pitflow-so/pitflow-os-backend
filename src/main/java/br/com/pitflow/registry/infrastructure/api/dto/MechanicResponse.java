package br.com.pitflow.registry.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados de retorno do mecânico cadastrado")
public record MechanicResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome do mecânico", example = "João Silva")
        String name,

        @Schema(description = "Username do mecânico", example = "joaosilva")
        String username
) {
}
