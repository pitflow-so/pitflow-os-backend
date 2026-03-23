package br.com.pitflow.inventory.presenter;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.presenter.dto.PartResponse;

public class PartPresenter {
    private PartPresenter() {}

    public static PartResponse toResponse(Part part) {
        return new PartResponse(part.getId(), part.getSku(), part.getName(),
                part.getDescription(), part.getPrice(), part.getStockQuantity());
    }

}
