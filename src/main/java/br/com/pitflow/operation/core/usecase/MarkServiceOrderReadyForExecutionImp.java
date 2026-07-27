package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.event.ServiceOrderReadyForExecution;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderReadyForExecution;

import java.time.Clock;
import java.util.UUID;

public final class MarkServiceOrderReadyForExecutionImp
        implements MarkServiceOrderReadyForExecution {
    private final ServiceOrderGateway orders;
    private final OperationEventGateway events;
    private final TransactionGateway transactions;
    private final Clock clock;

    public MarkServiceOrderReadyForExecutionImp(
            ServiceOrderGateway orders,
            OperationEventGateway events,
            TransactionGateway transactions,
            Clock clock
    ) {
        this.orders = orders;
        this.events = events;
        this.transactions = transactions;
        this.clock = clock;
    }

    @Override
    public Result execute(Command command) {
        validate(command);
        return transactions.execute(() -> transitionAndRegister(command));
    }

    private Result transitionAndRegister(Command command) {
        var order = orders.findById(command.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service Order not found with ID: "
                                + command.serviceOrderId()
                ));
        if (order.getStatus() == ServiceOrder.Status.READY_FOR_EXECUTION) {
            return Result.ALREADY_PROCESSED;
        }
        order.markReadyForExecution();
        orders.save(order);
        events.saveReadyForExecution(new ServiceOrderReadyForExecution(
                UUID.randomUUID(),
                command.correlationId(),
                command.messageId(),
                command.sagaId(),
                command.serviceOrderId(),
                command.paymentId(),
                clock.instant()
        ));
        return Result.UPDATED;
    }

    private static void validate(Command command) {
        if (command == null
                || command.messageId() == null
                || command.correlationId() == null
                || command.sagaId() == null
                || command.serviceOrderId() == null
                || command.paymentId() == null
                || command.externalPaymentId() == null
                || command.externalPaymentId().isBlank()
                || command.occurredAt() == null) {
            throw new IllegalArgumentException(
                    "Invalid MarkServiceOrderReadyForExecution command"
            );
        }
    }
}
