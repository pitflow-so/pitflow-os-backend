package br.com.pitflow.operation.infrastructure.config;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.operation.controller.ExternalDecisionController;
import br.com.pitflow.operation.controller.ServiceOrderController;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.usecase.AddOrderItemImp;
import br.com.pitflow.operation.core.usecase.ApproveOrderImp;
import br.com.pitflow.operation.core.usecase.CancelOrderImp;
import br.com.pitflow.operation.core.usecase.CompleteDiagnosisImp;
import br.com.pitflow.operation.core.usecase.CreateServiceOrderImp;
import br.com.pitflow.operation.core.usecase.CreateServiceOrderWithAllDataImp;
import br.com.pitflow.operation.core.usecase.DeliverOrderImp;
import br.com.pitflow.operation.core.usecase.FindAllServiceOrdersImp;
import br.com.pitflow.operation.core.usecase.FinishOrderImp;
import br.com.pitflow.operation.core.usecase.GetAverageExecutionTimeImp;
import br.com.pitflow.operation.core.usecase.GetServiceOrderByIdImp;
import br.com.pitflow.operation.core.usecase.GetServiceOrderDurationImp;
import br.com.pitflow.operation.core.usecase.ListInExecutionOrdersImp;
import br.com.pitflow.operation.core.usecase.ListPrioritizedServiceOrdersImp;
import br.com.pitflow.operation.core.usecase.StartDiagnosisImp;
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
import br.com.pitflow.operation.infrastructure.persistence.adapter.JpaServiceOrderGatewayAdapter;
import br.com.pitflow.operation.infrastructure.persistence.repository.SpringServiceOrderRepository;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanOperationConfig {

    @Bean
    public ServiceOrderGateway serviceOrderRepository(SpringServiceOrderRepository springRepository) {
        return new JpaServiceOrderGatewayAdapter(springRepository);
    }

    @Bean
    public AddOrderItem addOrderItem(
            ServiceOrderGateway repository,
            PartGateway partGateway,
            ServiceGateway serviceGateway) {
        return new AddOrderItemImp(repository, partGateway, serviceGateway);
    }

    @Bean
    public ApproveOrder approveOrder(ServiceOrderGateway repository) {
        return new ApproveOrderImp(repository);
    }

    @Bean
    public CancelOrder cancelOrder(ServiceOrderGateway repository) {
        return new CancelOrderImp(repository);
    }

    @Bean
    public CompleteDiagnosis completeDiagnosis(
            ServiceOrderGateway repository,
            NotificationGateway notificationGateway,
            TokenGateway tokenGateway,
            @Value("${api.url}") String apiUrl) {
        return new CompleteDiagnosisImp(repository, notificationGateway, tokenGateway, apiUrl);
    }

    @Bean
    public CreateServiceOrder createServiceOrder(
            ServiceOrderGateway repository,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway) {
        return new CreateServiceOrderImp(repository, customerGateway, vehicleGateway);
    }

    @Bean
    public DeliverOrder deliverOrder(ServiceOrderGateway repository) {
        return new DeliverOrderImp(repository);
    }

    @Bean
    public FindAllServiceOrders findAllServiceOrders(ServiceOrderGateway repository) {
        return new FindAllServiceOrdersImp(repository);
    }

    @Bean
    public FinishOrder finishOrder(NotificationGateway notificationGateway, ServiceOrderGateway repository) {
        return new FinishOrderImp(notificationGateway, repository);
    }

    @Bean
    public GetServiceOrderById getServiceOrderById(ServiceOrderGateway repository) {
        return new GetServiceOrderByIdImp(repository);
    }

    @Bean
    public StartDiagnosis startDiagnosis(ServiceOrderGateway repository) {
        return new StartDiagnosisImp(repository);
    }

    @Bean
    public ListInExecutionOrders listInExecutionOrders(ServiceOrderGateway repository) {
        return new ListInExecutionOrdersImp(repository);
    }

    @Bean
    public GetAverageExecutionTime getAverageExecutionTime(ServiceOrderGateway repository) {
        return new GetAverageExecutionTimeImp(repository);
    }

    @Bean
    public GetServiceOrderDuration getServiceOrderDuration(ServiceOrderGateway repository) {
        return new GetServiceOrderDurationImp(repository);
    }

    @Bean
    public ListPrioritizedServiceOrders listPrioritizedServiceOrders(ServiceOrderGateway gateway) {
        return new ListPrioritizedServiceOrdersImp(gateway);
    }

    @Bean
    public CreateServiceOrderWithAllData createServiceOrderWithAllData(
            CreateServiceOrder createServiceOrder,
            AddOrderItem addOrderItem,
            GetServiceOrderById getServiceOrderById,
            TransactionGateway transactionGateway
    ){
        return new CreateServiceOrderWithAllDataImp(
                createServiceOrder,
                addOrderItem,
                getServiceOrderById,
                transactionGateway
        );
    }

    @Bean
    public ServiceOrderController serviceOrderController(
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
        return new ServiceOrderController(
                createServiceOrder,
                addOrderItem,
                startDiagnosis,
                completeDiagnosis,
                approveOrder,
                finishOrder,
                deliverOrder,
                cancelOrder,
                getServiceOrderById,
                findAllServiceOrders,
                listInExecutionOrders,
                getAverageExecutionTime,
                getServiceOrderDuration,
                createServiceOrderWithAllData,
                listPrioritizedServiceOrders
        );
    }

    @Bean
    public ExternalDecisionController externalDecisionController(TokenGateway  tokenGateway, ServiceOrderController serviceOrderController) {
        return new  ExternalDecisionController(tokenGateway, serviceOrderController);
    }
}

