package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.ListParts;
import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;

import java.util.List;

public class ListPartsImp implements ListParts {
    private final PartGateway repository;
    public ListPartsImp(PartGateway repository) { this.repository = repository; }

    @Override
    public List<Part> execute() {
        return repository.findAll();
    }
}