package br.com.pitflow.operation.infrastructure.web;

import br.com.pitflow.operation.controller.ServiceOrderController;
import br.com.pitflow.operation.infrastructure.web.dto.*;
import br.com.pitflow.operation.presenter.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ServiceOrderRestAdapterTest {
    @Test
    void delegatesAllHttpEndpoints() {
        var controller = mock(ServiceOrderController.class);
        var adapter = new ServiceOrderRestAdapter(controller);
        var id = UUID.randomUUID();
        var response = new ServiceOrderResponse(id, UUID.randomUUID(), UUID.randomUUID(), "OS",
                "RECEIVED", BigDecimal.ZERO, LocalDateTime.now(), null, List.of(), null);
        var create = new CreateServiceOrderRequest(response.customerId(), response.vehicleId(), "OS");
        var createAll = new CreateServiceOrderAllDataRequest(
                response.customerId(), response.vehicleId(), "OS", List.of());
        var item = new AddOrderItemRequest(id, UUID.randomUUID(), 1, "PART");
        when(controller.create(create)).thenReturn(response);
        when(controller.getServiceOrderWithAllData(createAll)).thenReturn(response);
        when(controller.getServiceOrderById(id)).thenReturn(response);
        when(controller.getServiceOrderStatus(id)).thenReturn("RECEIVED");
        when(controller.getServiceOrders()).thenReturn(List.of(response));
        when(controller.getInExecutionOrders()).thenReturn(List.of(response));
        when(controller.getPrioritizedOrders()).thenReturn(List.of(response));
        when(controller.getAverageExecutionTime()).thenReturn(new ExecutionTimeMetricsResponse(1.0, "1m"));
        when(controller.getDuration(id)).thenReturn(new OrderDurationResponse(id, 1.0, "1m", true));

        assertEquals(HttpStatus.CREATED, adapter.create(create).getStatusCode());
        assertEquals(HttpStatus.CREATED, adapter.create(createAll).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, adapter.addItem(id, item).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, adapter.startDiagnosis(id).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, adapter.completeDiagnosis(id).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, adapter.finish(id).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, adapter.deliver(id).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, adapter.cancel(id, "motivo").getStatusCode());
        assertEquals(response, adapter.getById(id).getBody());
        assertEquals("RECEIVED", adapter.getStatusById(id).getBody());
        assertEquals(1, adapter.getAll().getBody().size());
        assertEquals(1, adapter.listInExecution().getBody().size());
        assertEquals(1.0, adapter.getAverageTime().getBody().averageTimeInMinutes());
        assertEquals(1.0, adapter.getDuration(id).getBody().durationInMinutes());
        assertEquals(1, adapter.getPrioritized().getBody().size());
        assertEquals(HttpStatus.NO_CONTENT,
                adapter.budgetDecision(id, new BudgetApprovalRequest(true, null)).getStatusCode());
    }
}
