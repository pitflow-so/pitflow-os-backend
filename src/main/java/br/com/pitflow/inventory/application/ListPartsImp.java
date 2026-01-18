package br.com.pitflow.inventory.application;

import br.com.pitflow.inventory.application.usecase.ListParts;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.domain.repository.PartRepository;

import java.util.List;

public class ListPartsImp implements ListParts {
    private final PartRepository repository;
    public ListPartsImp(PartRepository repository) { this.repository = repository; }

    @Override
    public List<Part> execute() {
        return repository.findAll();
    }
}