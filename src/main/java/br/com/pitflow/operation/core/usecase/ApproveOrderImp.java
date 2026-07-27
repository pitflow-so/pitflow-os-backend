package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.event.ServiceOrderBudgetApproved;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.usecase.inputPort.ApproveOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;

import java.util.UUID;

public class ApproveOrderImp implements ApproveOrder {

    private final ServiceOrderGateway repository;
    private final OperationEventGateway eventGateway;
    private final TransactionGateway transactionGateway;

    public ApproveOrderImp(
            ServiceOrderGateway repository,
            OperationEventGateway eventGateway,
            TransactionGateway transactionGateway
    ) {
        this.repository = repository;
        this.eventGateway = eventGateway;
        this.transactionGateway = transactionGateway;
    }

    @Override
    public void execute(UUID orderId) {
        transactionGateway.execute(() -> approveAndRegisterEvent(orderId));
    }

    private void approveAndRegisterEvent(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        os.approve();
        repository.save(os);
        eventGateway.saveBudgetApproved(ServiceOrderBudgetApproved.from(os));
    }
}
