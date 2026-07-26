package br.com.pitflow.operation.infrastructure.messaging.sqs;

import br.com.pitflow.common.core.gateway.MessagePublisherGateway;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqsMessagePublisherAdapter
        implements MessagePublisherGateway {
    private final SqsClient sqsClient;
    private final Map<String, String> queueUrls =
            new ConcurrentHashMap<>();

    public SqsMessagePublisherAdapter(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public void send(String destination, String payload) {
        String queueUrl = queueUrls.computeIfAbsent(
                destination,
                this::resolveQueueUrl
        );
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(payload)
                .build());
    }

    private String resolveQueueUrl(String queueName) {
        return sqsClient.getQueueUrl(builder ->
                builder.queueName(queueName)
        ).queueUrl();
    }
}
