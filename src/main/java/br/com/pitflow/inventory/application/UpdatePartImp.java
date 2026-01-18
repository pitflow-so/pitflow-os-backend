package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.UpdatePartDto;
import br.com.pitflow.inventory.application.usecase.UpdatePart;
import br.com.pitflow.inventory.domain.repository.PartRepository;

import java.util.UUID;

public class UpdatePartImp implements UpdatePart {
    private final PartRepository repository;

    public UpdatePartImp(PartRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID id, UpdatePartDto dto) {
        var part = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + id));

        // If the SKU changed, check the new SKU is not already in use
        if (!part.getSku().equals(dto.sku())) {
            repository.findBySku(dto.sku()).ifPresent(existing -> {
                throw new IllegalStateException("SKU " + dto.sku() + " is already in use by another part.");
            });
        }

        part.setSku(dto.sku());
        part.setName(dto.name());
        part.setDescription(dto.description());
        part.setPrice(dto.price());
        part.setStockQuantity(dto.stockQuantity());

        repository.save(part);
    }
}