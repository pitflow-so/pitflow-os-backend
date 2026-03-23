package br.com.pitflow.inventory.core.usecase.part.inputPort;

import br.com.pitflow.inventory.core.entity.Part;

public interface FindPartBySku {
    Part execute(String sku);
}
