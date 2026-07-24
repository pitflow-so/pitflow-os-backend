package br.com.pitflow.operation.infrastructure.registry;

import br.com.pitflow.operation.core.gateway.RegistryGateway;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;
import java.util.UUID;

public class HttpRegistryGatewayAdapter implements RegistryGateway {

    private final RestClient restClient;

    public HttpRegistryGatewayAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void validateCustomerVehicle(UUID customerId, UUID vehicleId) {
        findCustomer(customerId);
        var vehicle = findVehicle(vehicleId);

        if (!customerId.equals(vehicle.customerId())) {
            throw new IllegalStateException(
                    "The informed vehicle does not belong to the informed customer.");
        }
    }

    @Override
    public Optional<String> findCustomerEmail(UUID customerId) {
        return Optional.ofNullable(findCustomer(customerId).email());
    }

    private CustomerResponse findCustomer(UUID customerId) {
        try {
            var response = restClient.get()
                    .uri("/internal/registry/customers/{id}", customerId)
                    .retrieve()
                    .body(CustomerResponse.class);
            if (response == null) {
                throw new IllegalStateException("Registry returned an empty customer response.");
            }
            return response;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("Customer not found with ID: " + customerId, exception);
            }
            throw exception;
        }
    }

    private VehicleResponse findVehicle(UUID vehicleId) {
        try {
            var response = restClient.get()
                    .uri("/internal/registry/vehicles/{id}", vehicleId)
                    .retrieve()
                    .body(VehicleResponse.class);
            if (response == null) {
                throw new IllegalStateException("Registry returned an empty vehicle response.");
            }
            return response;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new IllegalArgumentException("Vehicle not found with ID: " + vehicleId, exception);
            }
            throw exception;
        }
    }

    record CustomerResponse(UUID id, String email) {
    }

    record VehicleResponse(UUID id, UUID customerId) {
    }
}
