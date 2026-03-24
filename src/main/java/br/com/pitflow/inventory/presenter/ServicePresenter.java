package br.com.pitflow.inventory.presenter;

import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.presenter.dto.ServiceResponse;

public class ServicePresenter {
    private ServicePresenter() {}

    public static ServiceResponse toResponse(Service entity) {
        return new ServiceResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice()
        );
    }
}
