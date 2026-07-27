package br.com.pitflow.operation.infrastructure.config;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.common.core.gateway.MessagePublisherGateway;
import br.com.pitflow.operation.controller.ExternalDecisionController;
import br.com.pitflow.operation.controller.ServiceOrderController;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.OperationEventGateway;
import br.com.pitflow.operation.core.gateway.InventoryGateway;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
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
import br.com.pitflow.operation.core.usecase.MarkServiceOrderAwaitingPaymentImp;
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
import br.com.pitflow.operation.core.usecase.inputPort.MarkServiceOrderAwaitingPayment;
import br.com.pitflow.operation.core.usecase.inputPort.StartDiagnosis;
import br.com.pitflow.operation.infrastructure.consumer.sqs.OperationCommandConsumer;
import br.com.pitflow.operation.infrastructure.metrics.MicrometerMetricsAdapter;
import br.com.pitflow.operation.infrastructure.inventory.HttpInventoryGatewayAdapter;
import br.com.pitflow.operation.infrastructure.persistence.adapter.JpaServiceOrderGatewayAdapter;
import br.com.pitflow.operation.infrastructure.persistence.repository.SpringServiceOrderRepository;
import br.com.pitflow.operation.infrastructure.outbox.JpaOperationEventGatewayAdapter;
import br.com.pitflow.operation.infrastructure.outbox.OutboxClaimService;
import br.com.pitflow.operation.infrastructure.outbox.OutboxPublicationService;
import br.com.pitflow.operation.infrastructure.outbox.OutboxPublisherScheduler;
import br.com.pitflow.operation.infrastructure.outbox.OutboxStateService;
import br.com.pitflow.operation.infrastructure.outbox.SpringOutboxRepository;
import br.com.pitflow.operation.infrastructure.messaging.sqs.SqsMessagePublisherAdapter;
import br.com.pitflow.operation.infrastructure.registry.HttpRegistryGatewayAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Clock;
import java.time.Duration;

@Configuration
@EnableScheduling
public class BeanOperationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public SqsClient sqsClient(
            @Value("${AWS_REGION:us-east-1}") String awsRegion
    ) {
        return SqsClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    public MessagePublisherGateway messagePublisherGateway(
            SqsClient sqsClient
    ) {
        return new SqsMessagePublisherAdapter(sqsClient);
    }

    @Bean
    public OutboxClaimService outboxClaimService(
            SpringOutboxRepository repository,
            Clock clock
    ) {
        return new OutboxClaimService(repository, clock);
    }

    @Bean
    public OutboxStateService outboxStateService(
            SpringOutboxRepository repository,
            Clock clock
    ) {
        return new OutboxStateService(repository, clock);
    }

    @Bean
    public OutboxPublicationService outboxPublicationService(
            MessagePublisherGateway publisherGateway,
            OutboxStateService stateService,
            @Value("${outbox.publisher.max-backoff-seconds:300}")
            int maxBackoffSeconds
    ) {
        return new OutboxPublicationService(
                publisherGateway,
                stateService,
                maxBackoffSeconds
        );
    }

    @Bean
    @ConditionalOnProperty(
            name = "outbox.publisher.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public OutboxPublisherScheduler outboxPublisherScheduler(
            OutboxClaimService claimService,
            OutboxPublicationService publicationService,
            @Value("${outbox.publisher.batch-size:10}") int batchSize,
            @Value("${outbox.publisher.lease-seconds:60}")
            long leaseSeconds
    ) {
        return new OutboxPublisherScheduler(
                claimService,
                publicationService,
                batchSize,
                Duration.ofSeconds(leaseSeconds)
        );
    }

    @Bean
    public RegistryGateway registryGateway(
            @Value("${services.registry.base-url}") String registryBaseUrl) {
        return new HttpRegistryGatewayAdapter(RestClient.builder().baseUrl(registryBaseUrl).build());
    }

    @Bean
    public InventoryGateway inventoryGateway(
            @Value("${services.inventory.base-url}") String inventoryBaseUrl) {
        return new HttpInventoryGatewayAdapter(RestClient.builder().baseUrl(inventoryBaseUrl).build());
    }

    @Bean
    public ServiceOrderGateway serviceOrderRepository(SpringServiceOrderRepository springRepository) {
        return new JpaServiceOrderGatewayAdapter(springRepository);
    }

    @Bean
    public AddOrderItem addOrderItem(
            ServiceOrderGateway repository,
            InventoryGateway inventoryGateway) {
        return new AddOrderItemImp(repository, inventoryGateway);
    }

    @Bean
    public OperationEventGateway operationEventGateway(
            SpringOutboxRepository repository,
            ObjectMapper objectMapper
    ) {
        return new JpaOperationEventGatewayAdapter(repository, objectMapper);
    }

    @Bean
    public ApproveOrder approveOrder(
            ServiceOrderGateway repository,
            OperationEventGateway eventGateway,
            TransactionGateway transactionGateway
    ) {
        return new ApproveOrderImp(
                repository,
                eventGateway,
                transactionGateway
        );
    }

    @Bean
    public MarkServiceOrderAwaitingPayment markServiceOrderAwaitingPayment(
            ServiceOrderGateway repository,
            OperationEventGateway eventGateway,
            RegistryGateway registryGateway,
            NotificationGateway notificationGateway,
            TransactionGateway transactionGateway,
            Clock clock
    ) {
        return new MarkServiceOrderAwaitingPaymentImp(
                repository,
                eventGateway,
                registryGateway,
                notificationGateway,
                transactionGateway,
                clock
        );
    }

    @Bean
    @ConditionalOnProperty(
            name = "operation.consumer.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public OperationCommandConsumer operationCommandConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            MarkServiceOrderAwaitingPayment markAwaitingPayment,
            @Value("${operation.consumer.queue-name:operation-command-queue}")
            String queueName,
            @Value("${operation.consumer.wait-time-seconds:20}")
            int waitTimeSeconds
    ) {
        return new OperationCommandConsumer(
                sqsClient,
                objectMapper,
                markAwaitingPayment,
                queueName,
                waitTimeSeconds
        );
    }

    @Bean
    public CancelOrder cancelOrder(ServiceOrderGateway repository) {
        return new CancelOrderImp(repository);
    }

    @Bean
    public CompleteDiagnosis completeDiagnosis(
            ServiceOrderGateway repository,
            NotificationGateway notificationGateway,
            ServiceOrderMetricsGateway metricsGateway,
            TokenGateway tokenGateway,
            RegistryGateway registryGateway,
            @Value("${api.url}") String apiUrl) {
        return new CompleteDiagnosisImp(
                repository, notificationGateway, metricsGateway, tokenGateway, registryGateway, apiUrl);
    }

    @Bean
    public CreateServiceOrder createServiceOrder(
            ServiceOrderGateway repository,
            RegistryGateway registryGateway,
            ServiceOrderMetricsGateway serviceOrderMetricsGateway
    ) {
        return new CreateServiceOrderImp(repository, registryGateway, serviceOrderMetricsGateway);
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
    public FinishOrder finishOrder(NotificationGateway notificationGateway,
                                   ServiceOrderGateway repository,
                                   ServiceOrderMetricsGateway metricsGateway,
                                   RegistryGateway registryGateway) {
        return new FinishOrderImp(notificationGateway, repository, metricsGateway, registryGateway);
    }

    @Bean
    public GetServiceOrderById getServiceOrderById(ServiceOrderGateway repository) {
        return new GetServiceOrderByIdImp(repository);
    }

    @Bean
    public StartDiagnosis startDiagnosis(ServiceOrderGateway repository, ServiceOrderMetricsGateway metricsGateway) {
        return new StartDiagnosisImp(repository, metricsGateway);
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

    @Bean
    public ServiceOrderMetricsGateway serviceOrderMetricsGateway(MeterRegistry meterRegistry) {
        return new MicrometerMetricsAdapter(meterRegistry);
    }
}
