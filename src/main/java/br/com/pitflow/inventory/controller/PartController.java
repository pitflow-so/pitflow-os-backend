package br.com.pitflow.inventory.controller;

import br.com.pitflow.inventory.controller.dto.UpdatePartCommand;
import br.com.pitflow.inventory.infrastructure.web.dto.CreatePartRequest;
import br.com.pitflow.inventory.core.usecase.part.inputPort.CreatePart;
import br.com.pitflow.inventory.core.usecase.part.inputPort.DeletePart;
import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartById;
import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartBySku;
import br.com.pitflow.inventory.core.usecase.part.inputPort.ListParts;
import br.com.pitflow.inventory.core.usecase.part.inputPort.UpdatePart;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdatePartRequest;
import br.com.pitflow.inventory.presenter.PartPresenter;
import br.com.pitflow.inventory.presenter.dto.PartResponse;
import br.com.pitflow.inventory.controller.dto.CreatePartCommand;

import java.util.List;
import java.util.UUID;

public class PartController {

    private final CreatePart createPart;
    private final FindPartById findPartById;
    private final FindPartBySku findPartBySku;
    private final ListParts listParts;
    private final UpdatePart updatePart;
    private final DeletePart deletePart;

    public PartController(
            CreatePart createPart,
            FindPartById findPartById,
            FindPartBySku findPartBySku,
            ListParts listParts,
            UpdatePart updatePart,
            DeletePart deletePart
    ) {
        this.createPart = createPart;
        this.findPartById = findPartById;
        this.findPartBySku = findPartBySku;
        this.listParts = listParts;
        this.updatePart = updatePart;
        this.deletePart = deletePart;
    }

    public PartResponse create(CreatePartRequest dto){
        var command = new CreatePartCommand(
                dto.sku(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.initialStock()
        );
        var part = createPart.execute(command);
        return PartPresenter.toResponse(part);
    }

    public PartResponse findPartById(UUID id){
        var part = findPartById.execute(id);
        return PartPresenter.toResponse(part);
    }

    public PartResponse findPartBySku(String sku){
        var part = findPartBySku.execute(sku);
        return PartPresenter.toResponse(part);
    }

    public List<PartResponse> listParts(){
        var list = listParts.execute();
        return list.stream().map(PartPresenter::toResponse).toList();
    }

    public PartResponse updatePart(UUID id, UpdatePartRequest dto){
        var command = new UpdatePartCommand(
                dto.sku(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.stockQuantity()
        );

        updatePart.execute(id, command);
        var part = findPartById.execute(id);
        return PartPresenter.toResponse(part);
    }

    public void deletePart(UUID id){
        deletePart.execute(id);
    }
}