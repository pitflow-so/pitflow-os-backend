package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateServiceOrderImpTest {

    private ServiceOrderGateway serviceOrderGateway;
    private RegistryGateway registryGateway;
    private CreateServiceOrderImp createServiceOrder;
    private ServiceOrderMetricsGateway serviceOrderMetricsGateway;

    @BeforeEach
    void setUp() {
        serviceOrderGateway = mock(ServiceOrderGateway.class);
        registryGateway = mock(RegistryGateway.class);
        serviceOrderMetricsGateway = mock(ServiceOrderMetricsGateway.class);
        createServiceOrder = new CreateServiceOrderImp(
                serviceOrderGateway, registryGateway, serviceOrderMetricsGateway);
    }

    @Test
    @DisplayName("Deve criar uma OS com sucesso quando cliente e veículo existem e estão vinculados")
    void shouldCreateServiceOrderSuccessfully() {
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        var dto = new CreateServiceOrderCommand(customerId, vehicleId, "Dummy Problem");

        var result = createServiceOrder.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ServiceOrder.Status.RECEIVED);
        verify(serviceOrderGateway).save(any(ServiceOrder.class));
        verify(registryGateway).validateCustomerVehicle(customerId, vehicleId);
    }

    @Test
    @DisplayName("Deve falhar ao criar OS se o veículo não pertencer ao cliente")
    void shouldFailIfVehicleDoesNotBelongToCustomer() {
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        var dto = new CreateServiceOrderCommand(customerId, vehicleId, "Dummy Problem");

        doThrow(new IllegalStateException(
                "The informed vehicle does not belong to the informed customer."))
                .when(registryGateway).validateCustomerVehicle(customerId, vehicleId);

        assertThatThrownBy(() -> createServiceOrder.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The informed vehicle does not belong to the informed customer.");
    }
}
