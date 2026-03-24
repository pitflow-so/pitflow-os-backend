package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.controller.dto.CreateServiceOrderAllDataCommand;
import br.com.pitflow.operation.controller.dto.CreateServiceOrderAllDataCommand.ServiceOrderItemCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.usecase.inputPort.AddOrderItem;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrder;
import br.com.pitflow.operation.core.usecase.inputPort.GetServiceOrderById;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateServiceOrderWithAllDataImpTest {
    private CreateServiceOrder createServiceOrder;
    private AddOrderItem addOrderItem;
    private GetServiceOrderById getServiceOrderById;
    private TransactionGateway transactionGateway;

    private CreateServiceOrderWithAllDataImp useCase;

    @BeforeEach
    void setup() {
        createServiceOrder = mock(CreateServiceOrder.class);
        addOrderItem = mock(AddOrderItem.class);
        getServiceOrderById = mock(GetServiceOrderById.class);
        transactionGateway = mock(TransactionGateway.class);

        useCase = new CreateServiceOrderWithAllDataImp(
                createServiceOrder,
                addOrderItem,
                getServiceOrderById,
                transactionGateway
        );
    }

    @Test
    void shouldCreateServiceOrderWithItemsSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        UUID catalogId = UUID.randomUUID();

        var command = new CreateServiceOrderAllDataCommand(
                customerId,
                vehicleId,
                "Troca de óleo",
                List.of(new ServiceOrderItemCommand(catalogId, 2, "PART"))
        );

        ServiceOrder createdOrder = mock(ServiceOrder.class);
        when(createdOrder.getId()).thenReturn(orderId);

        ServiceOrder finalOrder = mock(ServiceOrder.class);

        when(createServiceOrder.execute(any())).thenReturn(createdOrder);
        when(getServiceOrderById.execute(orderId)).thenReturn(finalOrder);

        // simula execução da transação
        when(transactionGateway.execute(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<ServiceOrder> supplier = invocation.getArgument(0);
                    return supplier.get();
                });

        // Act
        ServiceOrder result = useCase.execute(command);

        //Assert
        assertEquals(finalOrder, result);

        // Verify
        verify(createServiceOrder).execute(any());
        verify(addOrderItem, times(1)).execute(any());
        verify(getServiceOrderById).execute(orderId);
        verify(transactionGateway).execute(any(Supplier.class));
    }
}
