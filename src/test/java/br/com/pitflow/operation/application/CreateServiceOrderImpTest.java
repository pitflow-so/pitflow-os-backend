package br.com.pitflow.operation.application;

import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.CreateServiceOrderImp;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateServiceOrderImpTest {

    private ServiceOrderGateway serviceOrderGateway;
    private CustomerGateway customerGateway;
    private VehicleGateway vehicleGateway;
    private CreateServiceOrderImp createServiceOrder;

    @BeforeEach
    void setUp() {
        serviceOrderGateway = mock(ServiceOrderGateway.class);
        customerGateway = mock(CustomerGateway.class);
        vehicleGateway = mock(VehicleGateway.class);
        createServiceOrder = new CreateServiceOrderImp(serviceOrderGateway, customerGateway, vehicleGateway);
    }

    @Test
    @DisplayName("Deve criar uma OS com sucesso quando cliente e veículo existem e estão vinculados")
    void shouldCreateServiceOrderSuccessfully() {
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        var dto = new CreateServiceOrderCommand(customerId, vehicleId, "Dummy Problem");

        var vehicleMock = mock(Vehicle.class);
        when(customerGateway.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleGateway.findById(vehicleId)).thenReturn(Optional.of(vehicleMock));
        when(vehicleMock.getCustomerId()).thenReturn(customerId);

        var result = createServiceOrder.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ServiceOrder.Status.RECEIVED);
        verify(serviceOrderGateway).save(any(ServiceOrder.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar OS se o veículo não pertencer ao cliente")
    void shouldFailIfVehicleDoesNotBelongToCustomer() {
        UUID customerId = UUID.randomUUID();
        UUID otherCustomerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        var dto = new CreateServiceOrderCommand(customerId, vehicleId, "Dummy Problem");

        var vehicleMock = mock(Vehicle.class);
        when(customerGateway.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleGateway.findById(vehicleId)).thenReturn(Optional.of(vehicleMock));
        when(vehicleMock.getCustomerId()).thenReturn(otherCustomerId); // Dono diferente

        assertThatThrownBy(() -> createServiceOrder.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The informed vehicle does not belong to the informed customer.");
    }
}