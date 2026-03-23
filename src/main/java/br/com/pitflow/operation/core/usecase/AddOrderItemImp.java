package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.inputPort.AddOrderItem;

public class AddOrderItemImp implements AddOrderItem {

    private final ServiceOrderGateway serviceOrderGateway;
    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;

    public AddOrderItemImp(
            ServiceOrderGateway serviceOrderGateway,
            PartGateway partGateway,
            ServiceGateway serviceGateway) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.partGateway = partGateway;
        this.serviceGateway = serviceGateway;
    }

    @Override
    public void execute(AddOrderItemCommand dto) {
        var os = serviceOrderGateway.findById(dto.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found"));

        if (ServiceOrder.ItemType.PART.name().equals(dto.type())) {
            var part = partGateway.findById(dto.catalogId())
                    .orElseThrow(() -> new IllegalArgumentException("Part not found in inventory"));

            // Remove from inventory
            part.removeStock(dto.quantity());

            // Add to order
            os.addPart(part.getId(), part.getName(), part.getPrice(), dto.quantity());

            // Update part stock in inventory
            partGateway.save(part);

        } else {
            var service = serviceGateway.findById(dto.catalogId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found in inventory"));

            // Add to order
            os.addService(service.getId(), service.getName(), service.getPrice());
        }

        serviceOrderGateway.save(os);
    }
}