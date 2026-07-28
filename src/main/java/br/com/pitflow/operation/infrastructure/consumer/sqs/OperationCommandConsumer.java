package br.com.pitflow.operation.infrastructure.consumer.sqs;

import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderReadyForExecution;
import br.com.pitflow.operation.core.usecase.inputPort.CancelServiceOrderForPaymentFailure;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.Instant;
import java.util.UUID;

public class OperationCommandConsumer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OperationCommandConsumer.class);
    private static final String MESSAGE_ID = "messageId";
    private static final String PAYLOAD = "payload";
    private static final String SAGA_ID = "sagaId";
    private static final String CORRELATION_ID = "correlationId";
    private static final String PAYMENT_ID = "paymentId";
    private static final String SERVICE_ORDER_ID = "serviceOrderId";
    private static final String OCCURRED_AT = "occurredAt";

    private final SqsClient sqs;
    private final ObjectMapper objectMapper;
    private final MarkServiceOrderAwaitingPayment markAwaitingPayment;
    private final MarkServiceOrderReadyForExecution markReadyForExecution;
    private final CancelServiceOrderForPaymentFailure cancelForPaymentFailure;
    private final String queueUrl;
    private final int waitTimeSeconds;

    public OperationCommandConsumer(
            SqsClient sqs,
            ObjectMapper objectMapper,
            MarkServiceOrderAwaitingPayment markAwaitingPayment,
            MarkServiceOrderReadyForExecution markReadyForExecution,
            CancelServiceOrderForPaymentFailure cancelForPaymentFailure,
            String queueName,
            int waitTimeSeconds
    ) {
        this.sqs = sqs;
        this.objectMapper = objectMapper;
        this.markAwaitingPayment = markAwaitingPayment;
        this.markReadyForExecution = markReadyForExecution;
        this.cancelForPaymentFailure = cancelForPaymentFailure;
        this.waitTimeSeconds = waitTimeSeconds;
        this.queueUrl = sqs.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build()).queueUrl();
    }

    @Scheduled(
            fixedDelayString = "${operation.consumer.delay-ms:1000}"
    )
    public void poll() {
        var response = sqs.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(waitTimeSeconds)
                .maxNumberOfMessages(10)
                .build());
        response.messages().forEach(this::process);
    }

    void process(Message message) {
        try {
            var root = objectMapper.readTree(message.body());
            validateEnvelope(root);
            var result = dispatch(root);
            sqs.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build());
            LOGGER.info(
                    "Operation command handled type={} messageId={} result={}",
                    root.path("type").asText(),
                    root.path(MESSAGE_ID).asText(),
                    result
            );
        } catch (RuntimeException exception) {
            LOGGER.warn(
                    "Operation command processing failed sqsMessageId={}",
                    message.messageId(),
                    exception
            );
        } catch (Exception exception) {
            LOGGER.warn(
                    "Invalid operation command sqsMessageId={}",
                    message.messageId(),
                    exception
            );
        }
    }

    private Object dispatch(JsonNode root) {
        return switch (root.path("type").asText()) {
            case "MarkServiceOrderAwaitingPayment" -> awaitingPayment(root);
            case "MarkServiceOrderReadyForExecution" -> readyForExecution(root);
            case "CancelServiceOrder" -> cancelServiceOrder(root);
            default -> throw new java.lang.UnsupportedOperationException(
                    "Unsupported operation command: "
                            + root.path("type").asText()
            );
        };
    }

    private Object cancelServiceOrder(JsonNode root) {
        var payload = root.path(PAYLOAD);
        return cancelForPaymentFailure.execute(new CancelServiceOrderForPaymentFailure.Command(
                uuid(root, MESSAGE_ID), uuid(root, CORRELATION_ID), uuid(root, SAGA_ID),
                uuid(root, SERVICE_ORDER_ID), uuid(payload, PAYMENT_ID),
                requiredText(payload, "reason"), instant(root, OCCURRED_AT)));
    }

    private Object awaitingPayment(JsonNode root) {
        var payload = root.path(PAYLOAD);
        return markAwaitingPayment.execute(
                    new MarkServiceOrderAwaitingPayment.Command(
                            uuid(root, MESSAGE_ID),
                            uuid(root, CORRELATION_ID),
                            uuid(root, SAGA_ID),
                            uuid(root, SERVICE_ORDER_ID),
                            uuid(payload, PAYMENT_ID),
                            requiredText(payload, "preferenceId"),
                            requiredText(payload, "checkoutUrl"),
                            instant(payload, "expiresAt"),
                            instant(root, OCCURRED_AT)
                    )
            );
    }

    private Object readyForExecution(JsonNode root) {
        var payload = root.path(PAYLOAD);
        return markReadyForExecution.execute(
                new MarkServiceOrderReadyForExecution.Command(
                        uuid(root, MESSAGE_ID),
                        uuid(root, CORRELATION_ID),
                        uuid(root, SAGA_ID),
                        uuid(root, SERVICE_ORDER_ID),
                        uuid(payload, PAYMENT_ID),
                        requiredText(payload, "externalPaymentId"),
                        instant(root, OCCURRED_AT)
                )
        );
    }

    private void validateEnvelope(JsonNode root) {
        if (root.path("schemaVersion").asInt() != 1) {
            throw new IllegalArgumentException(
                    "Unsupported schema version"
            );
        }
        requiredText(root, MESSAGE_ID);
        requiredText(root, CORRELATION_ID);
        requiredText(root, SAGA_ID);
        requiredText(root, SERVICE_ORDER_ID);
        requiredText(root, OCCURRED_AT);
        requiredText(root, "type");
    }

    private String requiredText(JsonNode node, String field) {
        var value = node.path(field).asText();
        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Required field is missing: " + field
            );
        }
        return value;
    }

    private UUID uuid(JsonNode node, String field) {
        return UUID.fromString(requiredText(node, field));
    }

    private Instant instant(JsonNode node, String field) {
        return Instant.parse(requiredText(node, field));
    }
}
