package br.com.pitflow.inventory.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record UpdateServiceDto(
        @Schema(example = "Limpeza de Bicos") String name,
        @Schema(example = "Limpeza técnica via ultrassom") String description,
        @Schema(example = "180.00") BigDecimal price
) {}