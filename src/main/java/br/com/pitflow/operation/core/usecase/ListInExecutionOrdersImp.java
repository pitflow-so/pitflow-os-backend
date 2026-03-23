package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.usecase.inputPort.ListInExecutionOrders;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.List;

public class ListInExecutionOrdersImp implements ListInExecutionOrders {

    private final ServiceOrderGateway repository;

    public ListInExecutionOrdersImp(ServiceOrderGateway repository) {
        this.repository = repository;
    }

    @Override
    public List<ServiceOrder> execute() {
        // Fetch all service orders with status IN_EXECUTION ordered by creation date ascending
        // To prioritize older orders for processing by mechanics
        return repository.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION);
    }
}