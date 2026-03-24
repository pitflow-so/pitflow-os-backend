package br.com.pitflow.operation.controller;

import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;
import br.com.pitflow.operation.controller.dto.CancelOrderCommand;
import br.com.pitflow.operation.controller.dto.CreateServiceOrderAllDataCommand;
import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.usecase.inputPort.AddOrderItem;
import br.com.pitflow.operation.core.usecase.inputPort.ApproveOrder;
import br.com.pitflow.operation.core.usecase.inputPort.CancelOrder;
import br.com.pitflow.operation.core.usecase.inputPort.CompleteDiagnosis;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrder;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrderWithAllData;
import br.com.pitflow.operation.core.usecase.inputPort.DeliverOrder;
import br.com.pitflow.operation.core.usecase.inputPort.FindAllServiceOrders;
import br.com.pitflow.operation.core.usecase.inputPort.FinishOrder;
import br.com.pitflow.operation.core.usecase.inputPort.GetAverageExecutionTime;
import br.com.pitflow.operation.core.usecase.inputPort.GetServiceOrderById;
import br.com.pitflow.operation.core.usecase.inputPort.GetServiceOrderDuration;
import br.com.pitflow.operation.core.usecase.inputPort.ListInExecutionOrders;
import br.com.pitflow.operation.core.usecase.inputPort.ListPrioritizedServiceOrders;
import br.com.pitflow.operation.core.usecase.inputPort.StartDiagnosis;
import br.com.pitflow.operation.infrastructure.web.dto.AddOrderItemRequest;
import br.com.pitflow.operation.infrastructure.web.dto.BudgetApprovalRequest;
import br.com.pitflow.operation.infrastructure.web.dto.CreateServiceOrderAllDataRequest;
import br.com.pitflow.operation.infrastructure.web.dto.CreateServiceOrderRequest;
import br.com.pitflow.operation.presenter.ServiceOrderPresenter;
import br.com.pitflow.operation.presenter.dto.ExecutionTimeMetricsResponse;
import br.com.pitflow.operation.presenter.dto.OrderDurationResponse;
import br.com.pitflow.operation.presenter.dto.ServiceOrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static br.com.pitflow.operation.controller.dto.CreateServiceOrderAllDataCommand.ServiceOrderItemCommand;

public class ServiceOrderController {
    private final AddOrderItem addOrderItem;
    private final ApproveOrder approveOrder;
    private final CancelOrder cancelOrder;
    private final CompleteDiagnosis completeDiagnosis;
    private final CreateServiceOrder createServiceOrder;
    private final DeliverOrder deliverOrder;
    private final FindAllServiceOrders findAllServiceOrders;
    private final FinishOrder finishOrder;
    private final GetServiceOrderById getServiceOrderById;
    private final StartDiagnosis startDiagnosis;
    private final ListInExecutionOrders listInExecutionOrders;
    private final GetAverageExecutionTime getAverageExecutionTime;
    private final GetServiceOrderDuration getServiceOrderDuration;
    private final CreateServiceOrderWithAllData createServiceOrderWithAllData;
    private final ListPrioritizedServiceOrders listPrioritizedServiceOrders;

    public ServiceOrderController(
            CreateServiceOrder createServiceOrder,
            AddOrderItem addOrderItem,
            StartDiagnosis startDiagnosis,
            CompleteDiagnosis completeDiagnosis,
            ApproveOrder approveOrder,
            FinishOrder finishOrder,
            DeliverOrder deliverOrder,
            CancelOrder cancelOrder,
            GetServiceOrderById getServiceOrderById,
            FindAllServiceOrders findAllServiceOrders,
            ListInExecutionOrders listInExecutionOrders,
            GetAverageExecutionTime getAverageExecutionTime,
            GetServiceOrderDuration getServiceOrderDuration,
            CreateServiceOrderWithAllData createServiceOrderWithAllData,
            ListPrioritizedServiceOrders listPrioritizedServiceOrders
    ) {
        this.createServiceOrder = createServiceOrder;
        this.addOrderItem = addOrderItem;
        this.startDiagnosis = startDiagnosis;
        this.completeDiagnosis = completeDiagnosis;
        this.approveOrder = approveOrder;
        this.finishOrder = finishOrder;
        this.deliverOrder = deliverOrder;
        this.cancelOrder = cancelOrder;
        this.getServiceOrderById = getServiceOrderById;
        this.findAllServiceOrders = findAllServiceOrders;
        this.listInExecutionOrders = listInExecutionOrders;
        this.getAverageExecutionTime = getAverageExecutionTime;
        this.getServiceOrderDuration = getServiceOrderDuration;
        this.createServiceOrderWithAllData = createServiceOrderWithAllData;
        this.listPrioritizedServiceOrders = listPrioritizedServiceOrders;
    }

    public ServiceOrderResponse create(CreateServiceOrderRequest dto){
        var command = new CreateServiceOrderCommand(dto.customerId(), dto.vehicleId(), dto.description());
        var serviceOder = createServiceOrder.execute(command);
        return ServiceOrderPresenter.toResponse(serviceOder);
    }

    public void addOrderItem(UUID id, AddOrderItemRequest dto) {
        var command = new AddOrderItemCommand(id, dto.catalogId(), dto.quantity(), dto.type());
        addOrderItem.execute(command);
    }

    public void startDiagnosis(UUID id){
        startDiagnosis.execute(id);
    }

    public void completeDiagnosis(UUID id){
        completeDiagnosis.execute(id);
    }

    public void approve(UUID id){
        approveOrder.execute(id);
    }

    public void finish(UUID id){
        finishOrder.execute(id);
    }

    public void deliver(UUID id){
        deliverOrder.execute(id);
    }

    public void cancel(UUID id, String reason){
        var command =  new CancelOrderCommand(id, reason);
        cancelOrder.execute(command);
    }

    public ServiceOrderResponse getServiceOrderById(UUID id){
        var entity = getServiceOrderById.execute(id);
        return ServiceOrderPresenter.toResponse(entity);
    }

    public String getServiceOrderStatus(UUID id){
        var entity = getServiceOrderById.execute(id);
        return entity.getStatus().name();
    }

    public List<ServiceOrderResponse> getServiceOrders(){
        var list = findAllServiceOrders.execute();
        return list.stream().map(ServiceOrderPresenter::toResponse).toList();
    }

    public List<ServiceOrderResponse> getInExecutionOrders(){
        var list = listInExecutionOrders.execute();
        return list.stream().map(ServiceOrderPresenter::toResponse).toList();
    }

    public ExecutionTimeMetricsResponse getAverageExecutionTime(){
        var metrics = getAverageExecutionTime.execute();
        return ServiceOrderPresenter.toResponse(metrics);
    }

    public OrderDurationResponse getDuration(UUID id){
        var metrics = getServiceOrderDuration.execute(id);
        return ServiceOrderPresenter.toResponse(metrics);
    }

    //TODO: Identifiquei um problema transacional aqui (Erro ao inserir algum item gerar inconsistência na OS),
    // preciso pensar como resolver sem utilizar o @Transactional aqui
    public ServiceOrderResponse getServiceOrderWithAllData(CreateServiceOrderAllDataRequest dto){

        var orderItemsCommand = new ArrayList<ServiceOrderItemCommand>();

        for(var item : dto.orderItems() ){
            orderItemsCommand.add(
                    new ServiceOrderItemCommand(
                    item.catalogId(),
                    item.quantity(),
                    item.type()
                    )
            );
        }

        var command = new CreateServiceOrderAllDataCommand(
                dto.customerId(),
                dto.vehicleId(),
                dto.orderDescription(),
                orderItemsCommand
        );

        var serviceOrder = createServiceOrderWithAllData.execute(command);

        return ServiceOrderPresenter.toResponse(serviceOrder);
    }

    public List<ServiceOrderResponse> getPrioritizedOrders() {
        var list = listPrioritizedServiceOrders.execute();
        return list.stream()
                .map(ServiceOrderPresenter::toResponse)
                .toList();
    }

    public void processBudgetDecision(UUID id, BudgetApprovalRequest request) {

        if (request.approved()) {
            approveOrder.execute(id);
        } else {
            cancelOrder.execute(new CancelOrderCommand(id, request.reason()));
        }
    }
}
