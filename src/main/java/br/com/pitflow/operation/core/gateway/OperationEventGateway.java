package br.com.pitflow.operation.core.gateway;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;

public interface OperationEventGateway {
    void save(ServiceOrderBudgetApproved event);
}
