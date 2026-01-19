package br.com.pitflow.registry.application.usecases;

import java.util.UUID;

public interface DeleteCustomer {
    void execute(UUID id);
}
