package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.event.ServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.valueobject.Email;

import java.net.URI;
import java.time.Clock;
import java.util.UUID;

public final class MarkServiceOrderAwaitingPaymentImp
        implements MarkServiceOrderAwaitingPayment {
    private final ServiceOrderGateway orders;
    private final OperationEventGateway events;
    private final RegistryGateway registry;
    private final NotificationGateway notifications;
    private final TransactionGateway transactions;
    private final Clock clock;

    public MarkServiceOrderAwaitingPaymentImp(
            ServiceOrderGateway orders,
            OperationEventGateway events,
            RegistryGateway registry,
            NotificationGateway notifications,
            TransactionGateway transactions,
            Clock clock
    ) {
        this.orders = orders;
        this.events = events;
        this.registry = registry;
        this.notifications = notifications;
        this.transactions = transactions;
        this.clock = clock;
    }

    @Override
    public Result execute(Command command) {
        validate(command);
        var transition = transactions.execute(
                () -> transitionAndRegister(command)
        );
        sendPaymentLink(command);
        return transition;
    }

    private Result transitionAndRegister(Command command) {
        var order = orders.findById(command.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service Order not found with ID: "
                                + command.serviceOrderId()
                ));
        if (order.getStatus() == ServiceOrder.Status.AWAITING_PAYMENT) {
            return Result.ALREADY_PROCESSED;
        }
        order.markAwaitingPayment();
        orders.save(order);
        events.saveAwaitingPayment(new ServiceOrderAwaitingPayment(
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

    private void sendPaymentLink(Command command) {
        var order = orders.findById(command.serviceOrderId())
                .orElseThrow();
        var address = registry.findCustomerEmail(order.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer email not found for Service Order: "
                                + command.serviceOrderId()
                ));
        notifications.send(
                command.serviceOrderId(),
                new Notification(
                        "PitFlow - Pagamento da Ordem de Serviço",
                        paymentEmail(command),
                        new Email(address)
                )
        );
    }

    private String paymentEmail(Command command) {
        return """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
                  <h2>Pagamento da Ordem de Serviço</h2>
                  <p>Seu orçamento foi aprovado. Use o botão abaixo para realizar o pagamento.</p>
                  <p><a href="%s" style="background:#1f7a3f;color:#fff;padding:12px 20px;text-decoration:none;border-radius:6px">Pagar agora</a></p>
                  <p>Ordem de serviço: %s</p>
                  <p>Link válido até: %s</p>
                </div>
                """.formatted(
                command.checkoutUrl(),
                command.serviceOrderId(),
                command.expiresAt()
        );
    }

    private static void validate(Command command) {
        if (command == null
                || command.messageId() == null
                || command.correlationId() == null
                || command.sagaId() == null
                || command.serviceOrderId() == null
                || command.paymentId() == null
                || blank(command.preferenceId())
                || blank(command.checkoutUrl())
                || command.expiresAt() == null
                || command.occurredAt() == null) {
            throw new IllegalArgumentException(
                    "Invalid MarkServiceOrderAwaitingPayment command"
            );
        }
        var uri = URI.create(command.checkoutUrl());
        if (!"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null
                || !uri.getHost().toLowerCase().contains("mercadopago")) {
            throw new IllegalArgumentException(
                    "Invalid Mercado Pago checkout URL"
            );
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
