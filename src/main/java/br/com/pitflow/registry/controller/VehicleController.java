package br.com.pitflow.registry.controller;

import br.com.pitflow.registry.infrastructure.web.dto.AddVehicleRequest;
import br.com.pitflow.registry.controller.dto.AddVehicleCommand;
import br.com.pitflow.registry.controller.dto.UpdateVehicleCommand;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.AddVehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.DeleteVehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleById;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleByPlate;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehiclesByCustomerId;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.ListVehicles;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.UpdateVehicle;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateVehicleRequest;
import br.com.pitflow.registry.presenter.VehiclePresenter;
import br.com.pitflow.registry.presenter.dto.VehicleResponse;

import java.util.List;
import java.util.UUID;

public class VehicleController {

    public final AddVehicle addVehicle;
    private final UpdateVehicle updateVehicle;
    private final DeleteVehicle deleteVehicle;
    private final FindVehicleById findVehicleById;
    private final FindVehicleByPlate findVehicleByPlate;
    private final FindVehiclesByCustomerId findVehiclesByCustomerId;
    private final ListVehicles listVehicles;

    public VehicleController(AddVehicle addVehicle, UpdateVehicle updateVehicle, DeleteVehicle deleteVehicle, FindVehicleById findVehicleById, FindVehicleByPlate findVehicleByPlate, FindVehiclesByCustomerId findVehiclesByCustomerId, ListVehicles listVehicles) {
        this.addVehicle = addVehicle;
        this.updateVehicle = updateVehicle;
        this.deleteVehicle = deleteVehicle;
        this.findVehicleById = findVehicleById;
        this.findVehicleByPlate = findVehicleByPlate;
        this.findVehiclesByCustomerId = findVehiclesByCustomerId;
        this.listVehicles = listVehicles;
    }


    public VehicleResponse create(AddVehicleRequest dto) {
        var command = new AddVehicleCommand(
                dto.customerId(),
                dto.licensePlate(),
                dto.brand(),
                dto.model(),
                dto.year()
        );
        var vehicle = addVehicle.execute(command);
        return VehiclePresenter.toResponse(vehicle);
    }

    public VehicleResponse update(UUID id, UpdateVehicleRequest dto) {
        var command = new UpdateVehicleCommand(
                dto.brand(),
                dto.model(),
                dto.year(),
                dto.licensePlate()
        );

        updateVehicle.execute(id, command);
        var updated = findVehicleById.execute(id);
        return VehiclePresenter.toResponse(updated);
    }

    public void delete(UUID id) {
        deleteVehicle.execute(id);
    }

    public VehicleResponse findById(UUID id) {
        var vehicle = findVehicleById.execute(id);
        return VehiclePresenter.toResponse(vehicle);
    }

    public VehicleResponse findByPlate(String plate) {
       var vehicle = findVehicleByPlate.execute(plate);
        return VehiclePresenter.toResponse(vehicle);
    }

    public List<VehicleResponse> findByCustomerId(UUID customerId) {
        var list = findVehiclesByCustomerId.execute(customerId);
        return list.stream().map(VehiclePresenter::toResponse).toList();
    }

    public List<VehicleResponse> findAll() {
        var list = listVehicles.execute();
        return  list.stream().map(VehiclePresenter::toResponse).toList();
    }
}
