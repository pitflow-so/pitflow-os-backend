package br.com.pitflow.operation.core.gateway;

import java.util.Optional;
import java.util.UUID;

public interface RegistryGateway {
    void validateCustomerVehicle(UUID customerId, UUID vehicleId);

    Optional<String> findCustomerEmail(UUID customerId);
}
