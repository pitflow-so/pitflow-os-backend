package br.com.pitflow.operation.core.gateway;

import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.event.ServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.event.ServiceOrderReadyForExecution;

public interface OperationEventGateway {
    void saveBudgetApproved(ServiceOrderBudgetApproved event);
    void saveAwaitingPayment(ServiceOrderAwaitingPayment event);
    void saveReadyForExecution(ServiceOrderReadyForExecution event);
}
