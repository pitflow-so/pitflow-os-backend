package br.com.pitflow.registry.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criação de um novo cliente")
public record CreateCustomerRequest(
        @Schema(example = "Rafael Moreira", description = "Nome completo do cliente")
        String name,

        @Schema(example = "12345678910", description = "CPF ou CNPJ (apenas números)")
        String document,

        @Schema(example = "11996195936", description = "Telefone de contato com DDD")
        String phone,

        @Schema(example = "fulano@gmail.com", description = "E-mail para contato, que seja válido!")
        String email

) {}