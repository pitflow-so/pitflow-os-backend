package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.InventoryGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.AddOrderItem;

public class AddOrderItemImp implements AddOrderItem {
    private final ServiceOrderGateway serviceOrderGateway;
    private final InventoryGateway inventoryGateway;

    public AddOrderItemImp(ServiceOrderGateway serviceOrderGateway, InventoryGateway inventoryGateway) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.inventoryGateway = inventoryGateway;
    }

    @Override
    public void execute(AddOrderItemCommand dto) {
        var serviceOrder = serviceOrderGateway.findById(dto.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found"));

        if (ServiceOrder.ItemType.PART.name().equals(dto.type())) {
            var part = inventoryGateway.reservePart(dto.catalogId(), dto.quantity());
            serviceOrder.addPart(part.id(), part.name(), part.price(), dto.quantity());
        } else {
            var service = inventoryGateway.findService(dto.catalogId());
            serviceOrder.addService(service.id(), service.name(), service.price());
        }

        serviceOrderGateway.save(serviceOrder);
    }
}
