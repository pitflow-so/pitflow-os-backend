package br.com.pitflow.operation.core.gateway;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.event.ServiceOrderAwaitingPayment;

public interface OperationEventGateway {
    void saveBudgetApproved(ServiceOrderBudgetApproved event);
    void saveAwaitingPayment(ServiceOrderAwaitingPayment event);
}
