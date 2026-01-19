package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.dto.CancelOrderDto;
import br.com.pitflow.operation.application.usecase.CancelOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;

public class CancelOrderImp implements CancelOrder {

    private final ServiceOrderRepository repository;

    public CancelOrderImp(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(CancelOrderDto dto) {
        var os = repository.findById(dto.serviceOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + dto.serviceOrderId()));

        // Change status to CANCELLED
        os.cancel(dto.reason());

        repository.save(os);
    }
}