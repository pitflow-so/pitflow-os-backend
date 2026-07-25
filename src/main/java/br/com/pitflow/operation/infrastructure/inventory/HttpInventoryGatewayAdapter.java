package br.com.pitflow.operation.infrastructure.inventory;

import br.com.pitflow.operation.core.gateway.InventoryGateway;
import org.springframework.web.client.RestClient;

import java.util.UUID;

public class HttpInventoryGatewayAdapter implements InventoryGateway {
    private final RestClient restClient;

    public HttpInventoryGatewayAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CatalogItem reservePart(UUID partId, int quantity) {
        return restClient.post()
                .uri("/internal/inventory/parts/{id}/reservations", partId)
                .body(new ReservePartRequest(quantity))
                .retrieve()
                .body(CatalogItem.class);
    }

    @Override
    public CatalogItem findService(UUID serviceId) {
        return restClient.get()
                .uri("/internal/inventory/services/{id}", serviceId)
                .retrieve()
                .body(CatalogItem.class);
    }

    private record ReservePartRequest(int quantity) {
    }
}
