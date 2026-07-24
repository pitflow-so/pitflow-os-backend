package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrder;

public class CreateServiceOrderImp implements CreateServiceOrder {

    private final ServiceOrderGateway serviceOrderGateway;
    private final RegistryGateway registryGateway;
    private final ServiceOrderMetricsGateway metricsGateway;

    public CreateServiceOrderImp(
            ServiceOrderGateway serviceOrderGateway,
            RegistryGateway registryGateway,
            ServiceOrderMetricsGateway metricsGateway) {
        this.serviceOrderGateway = serviceOrderGateway;
        this.registryGateway = registryGateway;
        this.metricsGateway = metricsGateway;
    }

    @Override
    public ServiceOrder execute(CreateServiceOrderCommand dto) {
        registryGateway.validateCustomerVehicle(dto.customerId(), dto.vehicleId());

        var serviceOrder = new ServiceOrder(dto.customerId(), dto.vehicleId(), dto.description());

        serviceOrderGateway.save(serviceOrder);

        // Dispara a métrica de negócio para o Datadog
        metricsGateway.incrementOrderCreated();
        return serviceOrder;
    }
}
