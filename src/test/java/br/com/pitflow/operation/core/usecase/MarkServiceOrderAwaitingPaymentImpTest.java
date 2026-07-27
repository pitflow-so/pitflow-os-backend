package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.*;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderAwaitingPayment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MarkServiceOrderAwaitingPaymentImpTest {
    private final ServiceOrderGateway orders = mock(ServiceOrderGateway.class);
    private final OperationEventGateway events = mock(OperationEventGateway.class);
    private final RegistryGateway registry = mock(RegistryGateway.class);
    private final NotificationGateway notifications = mock(NotificationGateway.class);
    private final TransactionGateway transactions = new TransactionGateway() {
        @Override
        public <T> T execute(Supplier<T> operation) {
            return operation.get();
        }

        @Override
        public void execute(Runnable operation) {
            operation.run();
        }
    };
    private final Clock clock = Clock.fixed(
            Instant.parse("2026-07-27T03:00:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void transitionsPublishesConfirmationAndSendsPaymentLink() {
        var command = command();
        var order = order(ServiceOrder.Status.PAYMENT_PROCESSING);
        when(orders.findById(command.serviceOrderId()))
                .thenReturn(Optional.of(order));
        when(registry.findCustomerEmail(order.getCustomerId()))
                .thenReturn(Optional.of("customer@example.com"));

        var result = useCase().execute(command);

        assertThat(result).isEqualTo(
                MarkServiceOrderAwaitingPayment.Result.UPDATED
        );
        assertThat(order.getStatus())
                .isEqualTo(ServiceOrder.Status.AWAITING_PAYMENT);
        verify(orders).save(order);
        verify(events).saveAwaitingPayment(any(
                br.com.pitflow.operation.core.event
                        .ServiceOrderAwaitingPayment.class
        ));
        var notification = ArgumentCaptor.forClass(Notification.class);
        verify(notifications).send(
                eq(command.serviceOrderId()),
                notification.capture()
        );
        assertThat(notification.getValue().message())
                .contains(command.checkoutUrl());
        assertThat(notification.getValue().subject())
                .contains("Pagamento");
    }

    @Test
    void redeliveryDoesNotPublishDuplicateConfirmation() {
        var command = command();
        var order = order(ServiceOrder.Status.AWAITING_PAYMENT);
        when(orders.findById(command.serviceOrderId()))
                .thenReturn(Optional.of(order));
        when(registry.findCustomerEmail(order.getCustomerId()))
                .thenReturn(Optional.of("customer@example.com"));

        var result = useCase().execute(command);

        assertThat(result).isEqualTo(
                MarkServiceOrderAwaitingPayment.Result.ALREADY_PROCESSED
        );
        verify(orders, never()).save(any());
        verifyNoInteractions(events);
        verify(notifications).send(eq(command.serviceOrderId()), any());
    }

    private MarkServiceOrderAwaitingPaymentImp useCase() {
        return new MarkServiceOrderAwaitingPaymentImp(
                orders,
                events,
                registry,
                notifications,
                transactions,
                clock
        );
    }

    private ServiceOrder order(ServiceOrder.Status status) {
        var order = new ServiceOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Diagnóstico"
        );
        order.reconstituteStatus(status);
        return order;
    }

    private MarkServiceOrderAwaitingPayment.Command command() {
        return new MarkServiceOrderAwaitingPayment.Command(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "pref-1",
                "https://sandbox.mercadopago.com/checkout",
                Instant.parse("2026-07-28T03:00:00Z"),
                Instant.parse("2026-07-27T02:59:00Z")
        );
    }
}
