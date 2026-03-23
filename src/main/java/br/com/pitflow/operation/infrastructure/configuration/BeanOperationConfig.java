package br.com.pitflow.operation.infrastructure.configuration;

import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.operation.application.AddOrderItemImp;
import br.com.pitflow.operation.application.ApproveOrderImp;
import br.com.pitflow.operation.application.CancelOrderImp;
import br.com.pitflow.operation.application.CompleteDiagnosisImp;
import br.com.pitflow.operation.application.CreateServiceOrderImp;
import br.com.pitflow.operation.application.DeliverOrderImp;
import br.com.pitflow.operation.application.FindAllServiceOrdersImp;
import br.com.pitflow.operation.application.FinishOrderImp;
import br.com.pitflow.operation.application.GetAverageExecutionTimeImp;
import br.com.pitflow.operation.application.GetServiceOrderByIdImp;
import br.com.pitflow.operation.application.GetServiceOrderDurationImp;
import br.com.pitflow.operation.application.ListInExecutionOrdersImp;
import br.com.pitflow.operation.application.StartDiagnosisImp;
import br.com.pitflow.operation.application.usecase.AddOrderItem;
import br.com.pitflow.operation.application.usecase.ApproveOrder;
import br.com.pitflow.operation.application.usecase.CancelOrder;
import br.com.pitflow.operation.application.usecase.CompleteDiagnosis;
import br.com.pitflow.operation.application.usecase.CreateServiceOrder;
import br.com.pitflow.operation.application.usecase.DeliverOrder;
import br.com.pitflow.operation.application.usecase.FindAllServiceOrders;
import br.com.pitflow.operation.application.usecase.FinishOrder;
import br.com.pitflow.operation.application.usecase.GetAverageExecutionTime;
import br.com.pitflow.operation.application.usecase.GetServiceOrderById;
import br.com.pitflow.operation.application.usecase.GetServiceOrderDuration;
import br.com.pitflow.operation.application.usecase.ListInExecutionOrders;
import br.com.pitflow.operation.application.usecase.NotificationService;
import br.com.pitflow.operation.application.usecase.StartDiagnosis;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.operation.infrastructure.notifications.LogNotificationMock;
import br.com.pitflow.operation.infrastructure.persistence.adapter.JpaServiceOrderRepositoryAdapter;
import br.com.pitflow.operation.infrastructure.persistence.repository.SpringServiceOrderRepository;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanOperationConfig {

    @Bean
    public ServiceOrderRepository serviceOrderRepository(SpringServiceOrderRepository springRepository) {
        return new JpaServiceOrderRepositoryAdapter(springRepository);
    }

    @Bean
    public AddOrderItem addOrderItem(
            ServiceOrderRepository repository,
            PartGateway partGateway,
            ServiceGateway serviceGateway) {
        return new AddOrderItemImp(repository, partGateway, serviceGateway);
    }

    @Bean
    public ApproveOrder approveOrder(ServiceOrderRepository repository) {
        return new ApproveOrderImp(repository);
    }

    @Bean
    public CancelOrder cancelOrder(ServiceOrderRepository repository) {
        return new CancelOrderImp(repository);
    }

    @Bean
    public CompleteDiagnosis completeDiagnosis(
            ServiceOrderRepository repository,
            NotificationService notificationService) {
        return new CompleteDiagnosisImp(repository, notificationService);
    }

    @Bean
    public CreateServiceOrder createServiceOrder(
            ServiceOrderRepository repository,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway) {
        return new CreateServiceOrderImp(repository, customerGateway, vehicleGateway);
    }

    @Bean
    public DeliverOrder deliverOrder(ServiceOrderRepository repository) {
        return new DeliverOrderImp(repository);
    }

    @Bean
    public FindAllServiceOrders findAllServiceOrders(ServiceOrderRepository repository) {
        return new FindAllServiceOrdersImp(repository);
    }

    @Bean
    public FinishOrder finishOrder(NotificationService notificationService, ServiceOrderRepository repository) {
        return new FinishOrderImp(notificationService, repository);
    }

    @Bean
    public GetServiceOrderById getServiceOrderById(ServiceOrderRepository repository) {
        return new GetServiceOrderByIdImp(repository);
    }

    @Bean
    public NotificationService notificationService() {
        return new LogNotificationMock();
    }

    @Bean
    public StartDiagnosis startDiagnosis(ServiceOrderRepository repository) {
        return new StartDiagnosisImp(repository);
    }

    @Bean
    public ListInExecutionOrders listInExecutionOrders(ServiceOrderRepository repository) {
        return new ListInExecutionOrdersImp(repository);
    }

    @Bean
    public GetAverageExecutionTime getAverageExecutionTime(ServiceOrderRepository repository) {
        return new GetAverageExecutionTimeImp(repository);
    }

    @Bean
    public GetServiceOrderDuration getServiceOrderDuration(ServiceOrderRepository repository) {
        return new GetServiceOrderDurationImp(repository);
    }
}
