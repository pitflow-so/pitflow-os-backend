package br.com.pitflow.registry.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados de retorno do cliente cadastrado")
public record CustomerResponse (
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(example = "Rafael Moreira")
        String name,

        @Schema(example = "12345678909")
        String document,

        @Schema(example = "11996195936")
        String phone
) {}