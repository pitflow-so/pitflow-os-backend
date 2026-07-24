package br.com.pitflow.operation.core.gateway.dto;

import br.com.pitflow.operation.core.valueobject.Email;

public record Notification(String message, Email to) {
}
