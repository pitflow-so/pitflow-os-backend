package br.com.pitflow.common.core.gateway;

public interface MessagePublisherGateway {
    void send(String destination, String payload);
}
