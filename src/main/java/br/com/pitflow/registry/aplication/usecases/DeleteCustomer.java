package br.com.pitflow.registry.aplication.usecases;

import java.util.UUID;

public interface DeleteCustomer {
    void execute(UUID id);
}
