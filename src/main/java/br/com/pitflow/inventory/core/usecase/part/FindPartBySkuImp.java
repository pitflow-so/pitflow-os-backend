package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartBySku;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

public class FindPartBySkuImp implements FindPartBySku {
    private final PartGateway gateway;

    public FindPartBySkuImp(PartGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Part execute(String sku) {
        return gateway.findBySku(sku)
                .orElseThrow(() -> new IllegalStateException("Part with SKU " + sku + " not found."));
    }
}
