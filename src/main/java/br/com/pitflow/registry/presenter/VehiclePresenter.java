package br.com.pitflow.registry.presenter;

import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.presenter.dto.VehicleResponse;

public class VehiclePresenter {
    private VehiclePresenter() {}

    public static VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(vehicle.getId(), vehicle.getCustomerId(), vehicle.getLicensePlate().value(), vehicle.getBrand(), vehicle.getModel(), vehicle.getYear());
    }
}
