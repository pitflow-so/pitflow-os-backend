package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.FindAllServiceOrders;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.List;

public class FindAllServiceOrdersImp implements FindAllServiceOrders {

    private final ServiceOrderGateway repository;

    public FindAllServiceOrdersImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public List<ServiceOrder> execute() {
        // Fetch all service orders ordered by creation date descending
        // To ensure most recent orders appear first
        return repository.findAllByOrderByCreatedAtAsc();
    }
}