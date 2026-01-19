package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.usecase.ListInExecutionOrders;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

import java.util.List;

public class ListInExecutionOrdersImp implements ListInExecutionOrders {

    private final ServiceOrderRepository repository;

    public ListInExecutionOrdersImp(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ServiceOrder> execute() {
        // Fetch all service orders with status IN_EXECUTION ordered by creation date ascending
        // To prioritize older orders for processing by mechanics
        return repository.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION);
    }
}