package br.com.pitflow.operation.controller;

import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.enums.ExternalStatusEvent;
import br.com.pitflow.operation.core.usecase.inputPort.*;
import br.com.pitflow.operation.core.usecase.outputData.ExecutionTimeMetrics;
import br.com.pitflow.operation.core.usecase.outputData.OrderDurationMetrics;
import br.com.pitflow.operation.infrastructure.web.dto.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceOrderControllerTest {
    private final CreateServiceOrder create = mock(CreateServiceOrder.class);
    private final AddOrderItem add = mock(AddOrderItem.class);
    private final StartDiagnosis start = mock(StartDiagnosis.class);
    private final CompleteDiagnosis complete = mock(CompleteDiagnosis.class);
    private final ApproveOrder approve = mock(ApproveOrder.class);
    private final FinishOrder finish = mock(FinishOrder.class);
    private final DeliverOrder deliver = mock(DeliverOrder.class);
    private final CancelOrder cancel = mock(CancelOrder.class);
    private final GetServiceOrderById get = mock(GetServiceOrderById.class);
    private final FindAllServiceOrders all = mock(FindAllServiceOrders.class);
    private final ListInExecutionOrders executing = mock(ListInExecutionOrders.class);
    private final GetAverageExecutionTime average = mock(GetAverageExecutionTime.class);
    private final GetServiceOrderDuration duration = mock(GetServiceOrderDuration.class);
    private final CreateServiceOrderWithAllData createAll = mock(CreateServiceOrderWithAllData.class);
    private final ListPrioritizedServiceOrders prioritized = mock(ListPrioritizedServiceOrders.class);
    private final ServiceOrderController controller = new ServiceOrderController(
            create, add, start, complete, approve, finish, deliver, cancel, get, all,
            executing, average, duration, createAll, prioritized);

    @Test
    void createsReadsListsAndMapsMetrics() {
        var order = order();
        when(create.execute(any())).thenReturn(order);
        when(get.execute(order.getId())).thenReturn(order);
        when(all.execute()).thenReturn(List.of(order));
        when(executing.execute()).thenReturn(List.of(order));
        when(prioritized.execute()).thenReturn(List.of(order));
        when(average.execute()).thenReturn(new ExecutionTimeMetrics(12.5, "12m"));
        when(duration.execute(order.getId())).thenReturn(new OrderDurationMetrics(order.getId(), 7.0, "7m", true));

        assertEquals(order.getId(), controller.create(new CreateServiceOrderRequest(
                order.getCustomerId(), order.getVehicleId(), "Revisão")).id());
        assertEquals(order.getId(), controller.getServiceOrderById(order.getId()).id());
        assertEquals("RECEIVED", controller.getServiceOrderStatus(order.getId()));
        assertEquals(1, controller.getServiceOrders().size());
        assertEquals(1, controller.getInExecutionOrders().size());
        assertEquals(1, controller.getPrioritizedOrders().size());
        assertEquals(12.5, controller.getAverageExecutionTime().averageTimeInMinutes());
        assertEquals(7.0, controller.getDuration(order.getId()).durationInMinutes());
    }

    @Test
    void delegatesCommandsAndBudgetDecisions() {
        var id = UUID.randomUUID();
        controller.addOrderItem(id, new AddOrderItemRequest(id, UUID.randomUUID(), 2, "PART"));
        controller.startDiagnosis(id);
        controller.completeDiagnosis(id);
        controller.finish(id);
        controller.deliver(id);
        controller.cancel(id, "motivo");
        controller.processBudgetDecision(id, new BudgetApprovalRequest(true, null));
        controller.processBudgetDecision(id, new BudgetApprovalRequest(false, "recusado"));

        verify(add).execute(any());
        verify(start).execute(id);
        verify(complete).execute(id);
        verify(finish).execute(id);
        verify(deliver).execute(id);
        verify(cancel, times(2)).execute(any());
        verify(approve).execute(id);
    }

    @Test
    void createsWithItemsAndRoutesExternalEvents() {
        var order = order();
        var request = new CreateServiceOrderAllDataRequest(
                order.getCustomerId(), order.getVehicleId(), "Revisão",
                List.of(new CreateServiceOrderAllDataRequest.ServiceOrderItemRequest(
                        UUID.randomUUID(), 1, "SERVICE")));
        when(createAll.execute(any())).thenReturn(order);
        assertEquals(order.getId(), controller.getServiceOrderWithAllData(request).id());

        controller.processExternalStatusUpdate(new ExternalStatusUpdateRequest(
                order.getId(), ExternalStatusEvent.APPROVED, null));
        controller.processExternalStatusUpdate(new ExternalStatusUpdateRequest(
                order.getId(), ExternalStatusEvent.REJECTED, "falhou"));
        controller.processExternalStatusUpdate(new ExternalStatusUpdateRequest(
                order.getId(), ExternalStatusEvent.FINISHED, null));
        verify(approve).execute(order.getId());
        verify(cancel).execute(any());
        verify(finish).execute(order.getId());
    }

    private ServiceOrder order() {
        var order = new ServiceOrder(UUID.randomUUID(), UUID.randomUUID(), "Revisão");
        order.addService(UUID.randomUUID(), "Alinhamento", new BigDecimal("100.00"));
        return order;
    }
}
