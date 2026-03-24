package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.usecase.part.inputPort.DeletePart;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.UUID;

public class DeletePartImp implements DeletePart {
    private final PartGateway gateway;

    public DeletePartImp(PartGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UUID id) {
        gateway.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Cannot delete: Part not found with ID: " + id));

        gateway.deleteById(id);
    }
}