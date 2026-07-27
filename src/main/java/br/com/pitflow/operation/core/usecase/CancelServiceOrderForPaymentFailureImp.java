package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.event.ServiceOrderCancelled;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.CancelServiceOrderForPaymentFailure;

import java.time.Clock;
import java.util.UUID;

public final class CancelServiceOrderForPaymentFailureImp implements CancelServiceOrderForPaymentFailure {
    private final ServiceOrderGateway orders;
    private final OperationEventGateway events;
    private final TransactionGateway transactions;
    private final Clock clock;

    public CancelServiceOrderForPaymentFailureImp(ServiceOrderGateway orders, OperationEventGateway events,
                                                   TransactionGateway transactions, Clock clock) {
        this.orders = orders;
        this.events = events;
        this.transactions = transactions;
        this.clock = clock;
    }

    @Override
    public Result execute(Command command) {
        if (command == null || command.messageId() == null || command.correlationId() == null
                || command.sagaId() == null || command.serviceOrderId() == null
                || command.paymentId() == null || command.reason() == null
                || command.reason().isBlank() || command.occurredAt() == null) {
            throw new IllegalArgumentException("Invalid CancelServiceOrder command");
        }
        return transactions.execute(() -> {
            var order = orders.findById(command.serviceOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Service Order not found"));
            if (order.getStatus() == ServiceOrder.Status.CANCELLED) return Result.ALREADY_PROCESSED;
            if (order.getStatus() != ServiceOrder.Status.AWAITING_PAYMENT
                    && order.getStatus() != ServiceOrder.Status.PAYMENT_PROCESSING) {
                throw new IllegalStateException("Order cannot be compensated from " + order.getStatus());
            }
            order.cancel(command.reason());
            orders.save(order);
            events.saveCancelled(new ServiceOrderCancelled(UUID.randomUUID(), command.correlationId(),
                    command.messageId(), command.sagaId(), command.serviceOrderId(), command.paymentId(),
                    command.reason(), clock.instant()));
            return Result.UPDATED;
        });
    }
}
