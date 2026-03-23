package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.FindPartBySku;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

public class FindPartBySkuImp implements FindPartBySku {
    private final PartGateway repository;

    public FindPartBySkuImp(PartGateway repository) {
        this.repository = repository;
    }

    @Override
    public Part execute(String sku) {
        return repository.findBySku(sku)
                .orElseThrow(() -> new IllegalStateException("Part with SKU " + sku + " not found."));
    }
}
