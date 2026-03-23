package br.com.pitflow.inventory.application.usecase;

import br.com.pitflow.inventory.core.entity.Part;

public interface FindPartBySku {
    Part execute(String sku);
}
