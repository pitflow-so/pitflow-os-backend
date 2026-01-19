package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.FindAllServiceOrders;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.List;

public class FindAllServiceOrdersImp implements FindAllServiceOrders {

    private final ServiceOrderRepository repository;

    public FindAllServiceOrdersImp(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ServiceOrder> execute() {
        // Fetch all service orders ordered by creation date descending
        // To ensure most recent orders appear first
        return repository.findAllByOrderByCreatedAtAsc();
    }
}