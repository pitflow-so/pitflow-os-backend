package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.CreatePartDto;
import br.com.pitflow.inventory.application.usecase.CreatePart;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

public class CreatePartImp  implements CreatePart {

    private final PartGateway partGateway;

    public CreatePartImp(PartGateway partGateway) {
        this.partGateway = partGateway;
    }

    @Override
    public Part execute(CreatePartDto dto) {
        //SKU must be unique
        partGateway.findBySku(dto.sku()).ifPresent(p -> {
            throw new IllegalStateException("A part with SKU " + dto.sku() + " already exists.");
        });

        var part = new Part(
                dto.sku(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.initialStock()
        );

        partGateway.save(part);
        return part;
    }
}
