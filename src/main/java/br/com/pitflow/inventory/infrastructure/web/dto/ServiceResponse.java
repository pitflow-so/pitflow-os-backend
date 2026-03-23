package br.com.pitflow.inventory.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(
        UUID id,

        String name,

        String description,

        BigDecimal price
) {}