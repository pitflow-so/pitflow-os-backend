package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.FindPartBySku;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;

public class FindPartBySkuImp implements FindPartBySku {
    private final PartRepository repository;

    public FindPartBySkuImp(PartRepository repository) {
        this.repository = repository;
    }

    @Override
    public Part execute(String sku) {
        return repository.findBySku(sku)
                .orElseThrow(() -> new IllegalStateException("Part with SKU " + sku + " not found."));
    }
}
