package br.com.pitflow.registry.presenter;

import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.presenter.dto.MechanicResponse;

public class MechanicPresenter {
    private MechanicPresenter() {}

    public static MechanicResponse toResponse(Mechanic mechanic) {
        return new MechanicResponse(
                mechanic.getId(),
                mechanic.getName(),
                mechanic.getUsername()
        );
    }
}
