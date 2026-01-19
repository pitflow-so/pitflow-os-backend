package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateServiceOrderImpTest {

    private ServiceOrderRepository serviceOrderRepository;
    private CustomerRepository customerRepository;
    private VehicleRepository vehicleRepository;
    private CreateServiceOrderImp createServiceOrder;

    @BeforeEach
    void setUp() {
        serviceOrderRepository = mock(ServiceOrderRepository.class);
        customerRepository = mock(CustomerRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        createServiceOrder = new CreateServiceOrderImp(serviceOrderRepository, customerRepository, vehicleRepository);
    }

    @Test
    @DisplayName("Deve criar uma OS com sucesso quando cliente e veículo existem e estão vinculados")
    void shouldCreateServiceOrderSuccessfully() {
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        var dto = new CreateServiceOrderDto(customerId, vehicleId, "Dummy Problem");

        var vehicleMock = mock(Vehicle.class);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleMock));
        when(vehicleMock.getCustomerId()).thenReturn(customerId);

        var result = createServiceOrder.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ServiceOrder.Status.RECEIVED);
        verify(serviceOrderRepository).save(any(ServiceOrder.class));
    }

    @Test
    @DisplayName("Deve falhar ao criar OS se o veículo não pertencer ao cliente")
    void shouldFailIfVehicleDoesNotBelongToCustomer() {
        UUID customerId = UUID.randomUUID();
        UUID otherCustomerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        var dto = new CreateServiceOrderDto(customerId, vehicleId, "Dummy Problem");

        var vehicleMock = mock(Vehicle.class);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleMock));
        when(vehicleMock.getCustomerId()).thenReturn(otherCustomerId); // Dono diferente

        assertThatThrownBy(() -> createServiceOrder.execute(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The informed vehicle does not belong to the informed customer.");
    }
}