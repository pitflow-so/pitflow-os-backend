package br.com.pitflow.inventory.presenter.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PartResponse(
        UUID id,

        String sku,

        String name,

        String description,

        BigDecimal price,

        int stockQuantity
) {}