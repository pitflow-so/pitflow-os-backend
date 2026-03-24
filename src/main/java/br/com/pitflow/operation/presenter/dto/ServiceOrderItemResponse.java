package br.com.pitflow.operation.presenter.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceOrderItemResponse(
        UUID catalogId,

        String description,

        BigDecimal unitPrice,

        int quantity,

        BigDecimal totalPrice
) {}