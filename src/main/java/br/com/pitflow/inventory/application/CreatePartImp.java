package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.dto.CreatePartDto;
import br.com.pitflow.inventory.application.usecase.CreatePart;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;

public class CreatePartImp  implements CreatePart {

    private final PartRepository partRepository;

    public CreatePartImp(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public Part execute(CreatePartDto dto) {
        //SKU must be unique
        partRepository.findBySku(dto.sku()).ifPresent(p -> {
            throw new IllegalStateException("A part with SKU " + dto.sku() + " already exists.");
        });

        var part = new Part(
                dto.sku(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.initialStock()
        );

        partRepository.save(part);
        return part;
    }
}
