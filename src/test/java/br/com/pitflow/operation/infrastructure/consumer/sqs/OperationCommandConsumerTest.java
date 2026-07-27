package br.com.pitflow.operation.infrastructure.consumer.sqs;

import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderReadyForExecution;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OperationCommandConsumerTest {
    private final SqsClient sqs = mock(SqsClient.class);
    private final MarkServiceOrderAwaitingPayment useCase =
            mock(MarkServiceOrderAwaitingPayment.class);
    private final MarkServiceOrderReadyForExecution readyUseCase =
            mock(MarkServiceOrderReadyForExecution.class);
    private OperationCommandConsumer consumer;

    @BeforeEach
    void setUp() {
        when(sqs.getQueueUrl(any(GetQueueUrlRequest.class)))
                .thenReturn(GetQueueUrlResponse.builder()
                        .queueUrl("queue-url")
                        .build());
        consumer = new OperationCommandConsumer(
                sqs,
                new ObjectMapper().findAndRegisterModules(),
                useCase,
                readyUseCase,
                "operation-command-queue",
                1
        );
    }

    @Test
    void deletesMessageOnlyAfterSuccessfulHandling() {
        when(useCase.execute(any())).thenReturn(
                MarkServiceOrderAwaitingPayment.Result.UPDATED
        );
        var message = Message.builder()
                .messageId("sqs-1")
                .receiptHandle("receipt-1")
                .body("""
                        {
                          "schemaVersion": 1,
                          "messageId": "10000000-0000-0000-0000-000000000001",
                          "type": "MarkServiceOrderAwaitingPayment",
                          "occurredAt": "2026-07-27T03:00:00Z",
                          "correlationId": "20000000-0000-0000-0000-000000000002",
                          "sagaId": "30000000-0000-0000-0000-000000000003",
                          "serviceOrderId": "40000000-0000-0000-0000-000000000004",
                          "payload": {
                            "paymentId": "50000000-0000-0000-0000-000000000005",
                            "preferenceId": "pref-1",
                            "checkoutUrl": "https://sandbox.mercadopago.com/checkout",
                            "expiresAt": "2026-07-28T03:00:00Z"
                          }
                        }
                        """)
                .build();

        consumer.process(message);

        verify(useCase).execute(any());
        verify(sqs).deleteMessage(argThat(
                (DeleteMessageRequest request) ->
                        "receipt-1".equals(request.receiptHandle())
        ));
    }

    @Test
    void keepsFailedMessageForRetryAndDlq() {
        when(useCase.execute(any()))
                .thenThrow(new IllegalStateException("email failed"));
        var message = Message.builder()
                .messageId("sqs-1")
                .receiptHandle("receipt-1")
                .body("""
                        {
                          "schemaVersion": 1,
                          "messageId": "10000000-0000-0000-0000-000000000001",
                          "type": "MarkServiceOrderAwaitingPayment",
                          "occurredAt": "2026-07-27T03:00:00Z",
                          "correlationId": "20000000-0000-0000-0000-000000000002",
                          "sagaId": "30000000-0000-0000-0000-000000000003",
                          "serviceOrderId": "40000000-0000-0000-0000-000000000004",
                          "payload": {
                            "paymentId": "50000000-0000-0000-0000-000000000005",
                            "preferenceId": "pref-1",
                            "checkoutUrl": "https://sandbox.mercadopago.com/checkout",
                            "expiresAt": "2026-07-28T03:00:00Z"
                          }
                        }
                        """)
                .build();

        consumer.process(message);

        verify(sqs, never()).deleteMessage(any(DeleteMessageRequest.class));
    }
}
