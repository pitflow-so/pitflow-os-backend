package br.com.pitflow.operation.infrastructure.outbox;

import br.com.pitflow.common.core.gateway.MessagePublisherGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class OutboxPublicationService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OutboxPublicationService.class);

    private final MessagePublisherGateway publisherGateway;
    private final OutboxStateService stateService;
    private final int maxBackoffSeconds;

    public OutboxPublicationService(
            MessagePublisherGateway publisherGateway,
            OutboxStateService stateService,
            int maxBackoffSeconds
    ) {
        this.publisherGateway = publisherGateway;
        this.stateService = stateService;
        this.maxBackoffSeconds = maxBackoffSeconds;
    }

    public void publish(OutboxMessageJpa message, UUID lockId) {
        try {
            publisherGateway.send(
                    message.getDestination(),
                    message.getPayload()
            );
            stateService.markPublished(message.getId(), lockId);
            LOGGER.info(
                    "Outbox message published messageId={} type={}",
                    message.getId(),
                    message.getMessageType()
            );
        } catch (Exception exception) {
            LOGGER.warn(
                    "Outbox publication failed messageId={} attempt={}",
                    message.getId(),
                    message.getAttempts(),
                    exception
            );
            stateService.releaseForRetry(
                    message.getId(),
                    lockId,
                    message.getAttempts(),
                    maxBackoffSeconds,
                    exception.getMessage()
            );
        }
    }
}
