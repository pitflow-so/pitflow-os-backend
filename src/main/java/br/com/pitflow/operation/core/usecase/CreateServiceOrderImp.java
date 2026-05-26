package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrder;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;

public class CreateServiceOrderImp implements CreateServiceOrder {

    private final ServiceOrderGateway serviceOrderGateway;
    private final CustomerGateway customerGateway;
    private final VehicleGateway vehicleGateway;
    private final ServiceOrderMetricsGateway metricsGateway;

    public CreateServiceOrderImp(
            ServiceOrderGateway serviceOrderGateway,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway,
            ServiceOrderMetricsGateway metricsGateway) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.customerGateway = customerGateway;
        this.vehicleGateway = vehicleGateway;
        this.metricsGateway = metricsGateway;
    }

    @Override
    public ServiceOrder execute(CreateServiceOrderCommand dto) {
        customerGateway.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + dto.customerId()));

        var vehicle = vehicleGateway.findById(dto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + dto.vehicleId()));


        if (!vehicle.getCustomerId().equals(dto.customerId())) {
            throw new IllegalStateException("The informed vehicle does not belong to the informed customer.");
        }

        var serviceOrder = new ServiceOrder(dto.customerId(), dto.vehicleId(), dto.description());

        serviceOrderGateway.save(serviceOrder);

        // Dispara a métrica de negócio para o Datadog
        metricsGateway.incrementOrderCreated();
        return serviceOrder;
    }
}