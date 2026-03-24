package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.controller.dto.UpdatePartCommand;
import br.com.pitflow.inventory.core.usecase.part.inputPort.UpdatePart;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.UUID;

public class UpdatePartImp implements UpdatePart {
    private final PartGateway gateway;

    public UpdatePartImp(PartGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(UUID id, UpdatePartCommand dto) {
        var part = gateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + id));

        // If the SKU changed, check the new SKU is not already in use
        if (!part.getSku().equals(dto.sku())) {
            gateway.findBySku(dto.sku()).ifPresent(existing -> {
                throw new IllegalStateException("SKU " + dto.sku() + " is already in use by another part.");
            });
        }

        part.setSku(dto.sku());
        part.setName(dto.name());
        part.setDescription(dto.description());
        part.setPrice(dto.price());
        part.setStockQuantity(dto.stockQuantity());

        gateway.save(part);
    }
}