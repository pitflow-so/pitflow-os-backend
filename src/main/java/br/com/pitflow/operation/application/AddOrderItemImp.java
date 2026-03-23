package br.com.pitflow.operation.application;

import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.operation.application.dto.AddOrderItemDto;
import br.com.pitflow.operation.application.usecase.AddOrderItem;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

public class AddOrderItemImp implements AddOrderItem {

    private final ServiceOrderRepository serviceOrderRepository;
    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;

    public AddOrderItemImp(
            ServiceOrderRepository serviceOrderRepository,
            PartGateway partGateway,
            ServiceGateway serviceGateway) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.partGateway = partGateway;
        this.serviceGateway = serviceGateway;
    }

    @Override
    public void execute(AddOrderItemDto dto) {
        var os = serviceOrderRepository.findById(dto.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found"));

        if (dto.type() == ServiceOrder.ItemType.PART) {
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

        serviceOrderRepository.save(os);
    }
}