package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
import br.com.pitflow.operation.application.usecase.CreateServiceOrder;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;

public class CreateServiceOrderImp implements CreateServiceOrder {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerGateway customerGateway;
    private final VehicleGateway vehicleGateway;

    public CreateServiceOrderImp(
            ServiceOrderRepository serviceOrderRepository,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.customerGateway = customerGateway;
        this.vehicleGateway = vehicleGateway;
    }

    @Override
    public ServiceOrder execute(CreateServiceOrderDto dto) {
        customerGateway.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + dto.customerId()));

        var vehicle = vehicleGateway.findById(dto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + dto.vehicleId()));


        if (!vehicle.getCustomerId().equals(dto.customerId())) {
            throw new IllegalStateException("The informed vehicle does not belong to the informed customer.");
        }

        var serviceOrder = new ServiceOrder(dto.customerId(), dto.vehicleId(), dto.description());

        serviceOrderRepository.save(serviceOrder);
        return serviceOrder;
    }
}