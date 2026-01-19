package br.com.pitflow.registry.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de um novo cliente")
public record CreateCustomerDto(
        @Schema(example = "Rafael Moreira", description = "Nome completo do cliente")
        String name,

        @Schema(example = "12345678910", description = "CPF ou CNPJ (apenas números)")
        String document,

        @Schema(example = "11996195936", description = "Telefone de contato com DDD")
        String phone

) {}