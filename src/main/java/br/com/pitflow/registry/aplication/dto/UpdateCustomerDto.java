package br.com.pitflow.registry.aplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualização de um cliente")
public record UpdateCustomerDto(
        @Schema(description = "Nome completo do cliente", example = "João da Silva")
        String name,

        @Schema(description = "CPF ou CNPJ (apenas números)", example = "12345678901")
        String document,

        @Schema(description = "Telefone de contato", example = "11988887777")
        String phone
) {}
