package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartById;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.UUID;

public class FindPartByIdImp implements FindPartById {
    private final PartGateway gateway;
    public FindPartByIdImp(PartGateway gateway) { this.gateway = gateway; }

    @Override
    public Part execute(UUID id) {
        return gateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + id));
    }
}