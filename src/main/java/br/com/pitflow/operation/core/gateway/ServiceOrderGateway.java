package br.com.pitflow.operation.core.gateway;

import br.com.pitflow.operation.core.entity.ServiceOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderGateway {
    void save(ServiceOrder serviceOrder);
    Optional<ServiceOrder> findById(UUID id);
    List<ServiceOrder> findAll();
    List<ServiceOrder> findAllByOrderByCreatedAtAsc();
    List<ServiceOrder> findByCustomerId(UUID customerId);
    List<ServiceOrder> findByVehicleId(UUID vehicleId);
    List<ServiceOrder> findByStatusOrderByCreatedAtAsc(ServiceOrder.Status status);
    Double getAverageExecutionTimeInSeconds();
}
