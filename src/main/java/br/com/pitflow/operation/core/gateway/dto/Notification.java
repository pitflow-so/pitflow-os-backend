package br.com.pitflow.operation.core.gateway.dto;

import br.com.pitflow.operation.core.valueobject.Email;

public record Notification(String subject, String message, Email to) {
    public Notification(String message, Email to) {
        this("PitFlow - Ordem de Serviço", message, to);
    }
}
