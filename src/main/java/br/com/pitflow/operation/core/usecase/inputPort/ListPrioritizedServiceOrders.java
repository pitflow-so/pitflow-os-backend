package br.com.pitflow.operation.core.usecase.inputPort;

import br.com.pitflow.operation.core.entity.ServiceOrder;

import java.util.List;

public interface ListPrioritizedServiceOrders {
    List<ServiceOrder> execute();
}
