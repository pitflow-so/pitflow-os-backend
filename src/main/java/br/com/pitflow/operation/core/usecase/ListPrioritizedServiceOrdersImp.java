package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.ListPrioritizedServiceOrders;

import java.util.List;
import java.util.stream.Stream;

public class ListPrioritizedServiceOrdersImp implements ListPrioritizedServiceOrders {
    private final ServiceOrderGateway gateway;

    public ListPrioritizedServiceOrdersImp(ServiceOrderGateway gateway){
        this.gateway = gateway;
    }

    @Override
    public List<ServiceOrder> execute() {
        //TODO: I need create a new Gateway to find by List of Status or Ignored Status, to reduce queries
        var inExecution = gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_EXECUTION);
        var awaitingApproval = gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.AWAITING_APPROVAL);
        var inDiagnosis = gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.IN_DIAGNOSIS);
        var received = gateway.findByStatusOrderByCreatedAtAsc(ServiceOrder.Status.RECEIVED);

        return Stream.of(inExecution, awaitingApproval, inDiagnosis, received)
                .flatMap(List::stream)
                .toList();
    }
}
