package br.com.pitflow.operation.core.gateway;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryGateway {
    CatalogItem reservePart(UUID partId, int quantity);
    CatalogItem findService(UUID serviceId);

    record CatalogItem(UUID id, String name, BigDecimal price) {
    }
}
