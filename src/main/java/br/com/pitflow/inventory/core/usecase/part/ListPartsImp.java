package br.com.pitflow.inventory.core.usecase.part;

import br.com.pitflow.inventory.core.usecase.part.inputPort.ListParts;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.List;

public class ListPartsImp implements ListParts {
    private final PartGateway gateway;
    public ListPartsImp(PartGateway gateway) { this.gateway = gateway; }

    @Override
    public List<Part> execute() {
        return gateway.findAll();
    }
}