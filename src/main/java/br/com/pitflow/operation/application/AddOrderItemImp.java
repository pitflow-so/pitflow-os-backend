package br.com.pitflow.operation.application;

import br.com.pitflow.inventory.domain.repository.PartRepository;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;
import br.com.pitflow.operation.application.dto.AddOrderItemDto;
import br.com.pitflow.operation.application.usecase.AddOrderItem;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

public class AddOrderItemImp implements AddOrderItem {

    private final ServiceOrderRepository serviceOrderRepository;
    private final PartRepository partRepository;
    private final ServiceRepository serviceRepository;

    public AddOrderItemImp(
            ServiceOrderRepository serviceOrderRepository,
            PartRepository partRepository,
            ServiceRepository serviceRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.partRepository = partRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void execute(AddOrderItemDto dto) {
        var os = serviceOrderRepository.findById(dto.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found"));

        if (dto.type() == ServiceOrder.ItemType.PART) {
            var part = partRepository.findById(dto.catalogId())
                    .orElseThrow(() -> new IllegalArgumentException("Part not found in inventory"));

            // Remove from inventory
            part.removeStock(dto.quantity());

            // Add to order
            os.addPart(part.getId(), part.getName(), part.getPrice(), dto.quantity());

            // Update part stock in inventory
            partRepository.save(part);

        } else {
            var service = serviceRepository.findById(dto.catalogId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found in inventory"));

            // Add to order
            os.addService(service.getId(), service.getName(), service.getPrice());
        }

        serviceOrderRepository.save(os);
    }
}