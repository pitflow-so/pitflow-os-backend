package br.com.pitflow.registry.presenter.dto;

import java.util.UUID;

public record CustomerResponse (
        UUID id,

        String name,

        String document,

        String phone,

        String email,

        String status
) {}