package br.com.pitflow.inventory.controller;

import br.com.pitflow.inventory.controller.dto.CreateServiceCommand;
import br.com.pitflow.inventory.controller.dto.UpdateServiceCommand;
import br.com.pitflow.inventory.core.usecase.service.inputPort.CreateService;
import br.com.pitflow.inventory.core.usecase.service.inputPort.DeleteService;
import br.com.pitflow.inventory.core.usecase.service.inputPort.FindServiceById;
import br.com.pitflow.inventory.core.usecase.service.inputPort.ListServices;
import br.com.pitflow.inventory.core.usecase.service.inputPort.UpdateService;
import br.com.pitflow.inventory.infrastructure.web.dto.CreateServiceRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdateServiceRequest;
import br.com.pitflow.inventory.presenter.ServicePresenter;
import br.com.pitflow.inventory.presenter.dto.ServiceResponse;

import java.util.List;
import java.util.UUID;

public class ServiceController {

    private final CreateService createService;
    private final FindServiceById findServiceById;
    private final ListServices listServices;
    private final UpdateService updateService;
    private final DeleteService deleteService;

    public ServiceController(
            CreateService createService,
            FindServiceById findServiceById,
            ListServices listServices,
            UpdateService updateService,
            DeleteService deleteService
    ) {
        this.createService = createService;
        this.findServiceById = findServiceById;
        this.listServices = listServices;
        this.updateService = updateService;
        this.deleteService = deleteService;
    }

    public ServiceResponse create(CreateServiceRequest dto){
        var command = new CreateServiceCommand(
                dto.name(),
                dto.description(),
                dto.price()
        );
        var service = createService.execute(command);
        return ServicePresenter.toResponse(service);
    }

    public ServiceResponse findById(UUID id){
        var service = findServiceById.execute(id);
        return ServicePresenter.toResponse(service);
    }

    public List<ServiceResponse> findAll(){
        var list = listServices.execute();
        return list.stream().map(ServicePresenter::toResponse).toList();
    }

    public ServiceResponse update(UUID id, UpdateServiceRequest dto){
        var command = new UpdateServiceCommand(
                dto.name(),
                dto.description(),
                dto.price()
        );
        updateService.execute(id, command);
        var serviceUpdated = findServiceById.execute(id);
        return  ServicePresenter.toResponse(serviceUpdated);
    }

    public void delete(UUID id) {
        deleteService.execute(id);
    }
}
