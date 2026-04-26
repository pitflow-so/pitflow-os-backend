package br.com.pitflow.operation.core.gateway.dto;

import br.com.pitflow.registry.core.valueObject.Email;

public record Notification(String message, Email to) {
}
