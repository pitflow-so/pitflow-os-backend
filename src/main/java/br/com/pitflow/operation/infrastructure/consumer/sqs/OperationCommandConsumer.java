package br.com.pitflow.operation.infrastructure.consumer.sqs;

import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderReadyForExecution;
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

    private final SqsClient sqs;
    private final ObjectMapper objectMapper;
    private final MarkServiceOrderAwaitingPayment markAwaitingPayment;
    private final MarkServiceOrderReadyForExecution markReadyForExecution;
    private final String queueUrl;
    private final int waitTimeSeconds;

    public OperationCommandConsumer(
            SqsClient sqs,
            ObjectMapper objectMapper,
            MarkServiceOrderAwaitingPayment markAwaitingPayment,
            MarkServiceOrderReadyForExecution markReadyForExecution,
            String queueName,
            int waitTimeSeconds
    ) {
        this.sqs = sqs;
        this.objectMapper = objectMapper;
        this.markAwaitingPayment = markAwaitingPayment;
        this.markReadyForExecution = markReadyForExecution;
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
                    root.path("messageId").asText(),
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
            default -> throw new java.lang.UnsupportedOperationException(
                    "Unsupported operation command: "
                            + root.path("type").asText()
            );
        };
    }

    private Object awaitingPayment(JsonNode root) {
        var payload = root.path("payload");
        return markAwaitingPayment.execute(
                    new MarkServiceOrderAwaitingPayment.Command(
                            uuid(root, "messageId"),
                            uuid(root, "correlationId"),
                            uuid(root, "sagaId"),
                            uuid(root, "serviceOrderId"),
                            uuid(payload, "paymentId"),
                            requiredText(payload, "preferenceId"),
                            requiredText(payload, "checkoutUrl"),
                            instant(payload, "expiresAt"),
                            instant(root, "occurredAt")
                    )
            );
    }

    private Object readyForExecution(JsonNode root) {
        var payload = root.path("payload");
        return markReadyForExecution.execute(
                new MarkServiceOrderReadyForExecution.Command(
                        uuid(root, "messageId"),
                        uuid(root, "correlationId"),
                        uuid(root, "sagaId"),
                        uuid(root, "serviceOrderId"),
                        uuid(payload, "paymentId"),
                        requiredText(payload, "externalPaymentId"),
                        instant(root, "occurredAt")
                )
        );
    }

    private void validateEnvelope(JsonNode root) {
        if (root.path("schemaVersion").asInt() != 1) {
            throw new IllegalArgumentException(
                    "Unsupported schema version"
            );
        }
        requiredText(root, "messageId");
        requiredText(root, "correlationId");
        requiredText(root, "sagaId");
        requiredText(root, "serviceOrderId");
        requiredText(root, "occurredAt");
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
