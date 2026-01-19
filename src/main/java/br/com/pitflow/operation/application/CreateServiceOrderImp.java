package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
import br.com.pitflow.operation.application.usecase.CreateServiceOrder;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.domain.repository.VehicleRepository;

public class CreateServiceOrderImp implements CreateServiceOrder {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    public CreateServiceOrderImp(
            ServiceOrderRepository serviceOrderRepository,
            CustomerRepository customerRepository,
            VehicleRepository vehicleRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public ServiceOrder execute(CreateServiceOrderDto dto) {
        customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + dto.customerId()));

        var vehicle = vehicleRepository.findById(dto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + dto.vehicleId()));


        if (!vehicle.getCustomerId().equals(dto.customerId())) {
            throw new IllegalStateException("The informed vehicle does not belong to the informed customer.");
        }

        var serviceOrder = new ServiceOrder(dto.customerId(), dto.vehicleId(), dto.description());

        serviceOrderRepository.save(serviceOrder);
        return serviceOrder;
    }
}