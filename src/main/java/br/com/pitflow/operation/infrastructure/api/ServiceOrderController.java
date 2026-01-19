package br.com.pitflow.operation.infrastructure.api;

import br.com.pitflow.operation.application.dto.AddOrderItemDto;
import br.com.pitflow.operation.application.dto.CancelOrderDto;
import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
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
import br.com.pitflow.operation.application.usecase.StartDiagnosis;
import br.com.pitflow.operation.infrastructure.api.dto.ExecutionTimeMetricsResponse;
import br.com.pitflow.operation.infrastructure.api.dto.OrderDurationResponse;
import br.com.pitflow.operation.infrastructure.api.dto.ServiceOrderResponse;
import br.com.pitflow.operation.infrastructure.api.mapper.ServiceOrderApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operation/service-orders")
@Tag(name = "Operation - Service Orders", description = "Endpoints para gestão do ciclo de vida das Ordens de Serviço")
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
            GetServiceOrderDuration getServiceOrderDuration
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
    }

    @PostMapping
    @Operation(summary = "Abre uma nova Ordem de Serviço", description = "Status inicial: RECEIVED")
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody CreateServiceOrderDto dto) {
        var domain = createServiceOrder.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ServiceOrderApiMapper.toResponse(domain));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Adiciona peça ou serviço à OS", description = "Permitido apenas nos status RECEIVED ou IN_DIAGNOSIS")
    public ResponseEntity<Void> addItem(@PathVariable UUID id, @RequestBody AddOrderItemDto dto) {
        var updateDto = new AddOrderItemDto(id, dto.catalogId(), dto.quantity(), dto.type());
        addOrderItem.execute(updateDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/start-diagnosis")
    @Operation(summary = "Inicia a análise técnica, para definir serviços e peças", description = "Muda status para IN_DIAGNOSIS")
    public ResponseEntity<Void> startDiagnosis(@PathVariable UUID id) {
        startDiagnosis.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete-diagnosis")
    @Operation(summary = "Finaliza a análise técnica e notifica o cliente", description = "Muda status para AWAITING_APPROVAL")
    public ResponseEntity<Void> completeDiagnosis(@PathVariable UUID id) {
        completeDiagnosis.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Aprova o orçamento", description = "Muda status para IN_EXECUTION")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        approveOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finish")
    @Operation(summary = "Finaliza a execução dos serviços (Mão de Obra)", description = "Muda status para FINISHED")
    public ResponseEntity<Void> finish(@PathVariable UUID id) {
        finishOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deliver")
    @Operation(summary = "Registra a entrega do veículo", description = "Muda status para DELIVERED")
    public ResponseEntity<Void> deliver(@PathVariable UUID id) {
        deliverOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancela a Ordem de Serviço", description = "Exige motivo. Não permitido para OS finalizadas.")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, @RequestBody String reason) {
        cancelOrder.execute(new CancelOrderDto(id, reason));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca detalhes de uma OS específica")
    public ResponseEntity<ServiceOrderResponse> getById(@PathVariable UUID id) {
        var domain = getServiceOrderById.execute(id);
        return ResponseEntity.ok(ServiceOrderApiMapper.toResponse(domain));
    }

    @GetMapping
    @Operation(summary = "Lista todas as Ordens de Serviço da oficina, em ordem do mais antigo para o mais novo", description = "Mecânico pode vistualizar todas as ordens")
    public ResponseEntity<List<ServiceOrderResponse>> getAll() {
        //TODO: Will be implement pagination later
        var list = findAllServiceOrders.execute().stream()
                .map(ServiceOrderApiMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/in-execution")
    @Operation(
            summary = "Lista ordens prontas para execução",
            description = "Retorna a fila de trabalho do mecânico (Status: IN_EXECUTION) ordenada pela data de criação mais antiga."
    )
    public ResponseEntity<List<ServiceOrderResponse>> listInExecution() {
        var list = listInExecutionOrders.execute().stream()
                .map(ServiceOrderApiMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/metrics/average-execution-time")
    @Operation(
            summary = "Obter tempo médio de execução",
            description = "Calcula a média de tempo que os serviços levam para serem concluídos (do início da execução até a finalização técnica)."
    )
    public ResponseEntity<ExecutionTimeMetricsResponse> getAverageTime() {
        return ResponseEntity.ok(getAverageExecutionTime.execute());
    }

    @GetMapping("/{id}/duration")
    @Operation(
            summary = "Obter duração da OS",
            description = "Retorna o tempo decorrido desde o início da execução. Se a OS não foi finalizada, calcula o tempo até o momento atual."
    )
    public ResponseEntity<OrderDurationResponse> getDuration(@PathVariable UUID id) {
        return ResponseEntity.ok(getServiceOrderDuration.execute(id));
    }
}
