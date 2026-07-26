package br.com.pitflow.operation.infrastructure.web;

import br.com.pitflow.operation.infrastructure.web.dto.AddOrderItemRequest;
import br.com.pitflow.operation.controller.ServiceOrderController;
import br.com.pitflow.operation.infrastructure.web.dto.BudgetApprovalRequest;
import br.com.pitflow.operation.infrastructure.web.dto.CreateServiceOrderAllDataRequest;
import br.com.pitflow.operation.infrastructure.web.dto.CreateServiceOrderRequest;
import br.com.pitflow.operation.presenter.dto.ExecutionTimeMetricsResponse;
import br.com.pitflow.operation.presenter.dto.OrderDurationResponse;
import br.com.pitflow.operation.presenter.dto.ServiceOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@RestController
@RequestMapping("/service-orders")
@Tag(name = "Operation - Service Orders", description = "Endpoints para gestão do ciclo de vida das Ordens de Serviço")
public class ServiceOrderRestAdapter {

    private final ServiceOrderController controller;

    public ServiceOrderRestAdapter(ServiceOrderController controller){
        this.controller = controller;
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Abre uma nova Ordem de Serviço", description = "Status inicial: RECEIVED")
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody CreateServiceOrderRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(controller.create(dto));
    }

    @PostMapping("/v2")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Abre uma nova Ordem de Serviço já com os itens do catálago (Peça ou Serviço)", description = "Status inicial: RECEIVED")
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody CreateServiceOrderAllDataRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(controller.getServiceOrderWithAllData(dto));
    }

    @PostMapping("/{id}/items")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Adiciona peça ou serviço à OS", description = "Permitido apenas nos status RECEIVED ou IN_DIAGNOSIS")
    public ResponseEntity<Void> addItem(@PathVariable UUID id, @RequestBody AddOrderItemRequest dto) {
        controller.addOrderItem(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/start-diagnosis")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Inicia a análise técnica, para definir serviços e peças", description = "Muda status para IN_DIAGNOSIS")
    public ResponseEntity<Void> startDiagnosis(@PathVariable UUID id) {
        controller.startDiagnosis(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete-diagnosis")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Finaliza a análise técnica e notifica o cliente", description = "Muda status para AWAITING_APPROVAL")
    public ResponseEntity<Void> completeDiagnosis(@PathVariable UUID id) {
        controller.completeDiagnosis(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finish")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Finaliza a execução dos serviços (Mão de Obra)", description = "Muda status para FINISHED")
    public ResponseEntity<Void> finish(@PathVariable UUID id) {
        controller.finish(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deliver")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Registra a entrega do veículo", description = "Muda status para DELIVERED")
    public ResponseEntity<Void> deliver(@PathVariable UUID id) {
        controller.deliver(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Cancela a Ordem de Serviço", description = "Exige motivo para o cancelamento da OS.")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, @RequestBody String reason) {
        controller.cancel(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Busca detalhes de uma OS específica")
    public ResponseEntity<ServiceOrderResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(controller.getServiceOrderById(id));
    }

    @GetMapping("/status/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Busca o status da OS específica")
    public ResponseEntity<String> getStatusById(@PathVariable UUID id) {
        return ResponseEntity.ok(controller.getServiceOrderStatus(id));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Lista todas as Ordens de Serviço da oficina, em ordem do mais antigo para o mais novo", description = "Mecânico pode vistualizar todas as ordens")
    public ResponseEntity<List<ServiceOrderResponse>> getAll() {
        //TODO: Will be implement pagination later
        return ResponseEntity.ok(controller.getServiceOrders());
    }

    @GetMapping("/in-execution")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Lista ordens prontas para execução", description = "Retorna a fila de trabalho do mecânico (Status: IN_EXECUTION) ordenada pela data de criação mais antiga.")
    public ResponseEntity<List<ServiceOrderResponse>> listInExecution() {
        return ResponseEntity.ok(controller.getInExecutionOrders());
    }

    @GetMapping("/metrics/average-execution-time")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Obter tempo médio de execução", description = "Calcula a média de tempo que os serviços levam para serem concluídos (do início da execução até a finalização técnica).")
    public ResponseEntity<ExecutionTimeMetricsResponse> getAverageTime() {
        return ResponseEntity.ok(controller.getAverageExecutionTime());
    }

    @GetMapping("/{id}/duration")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Obter duração da OS", description = "Retorna o tempo decorrido desde o início da execução. Se a OS não foi finalizada, calcula o tempo até o momento atual.")
    public ResponseEntity<OrderDurationResponse> getDuration(@PathVariable UUID id) {
        return ResponseEntity.ok(controller.getDuration(id));
    }

    @GetMapping("v2/prioritized")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Lista ordens priorizadas",
            description = "Ordena por prioridade de status e data de criação"
    )
    public ResponseEntity<List<ServiceOrderResponse>> getPrioritized() {
        return ResponseEntity.ok(controller.getPrioritizedOrders());
    }

    @PatchMapping("v2/{id}/budget-decision")
    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            summary = "Recebe decisão do cliente sobre o orçamento, aceitar ou recusar",
            description = "Para integrações externas, utilize /operation/external/events"
    )
    public ResponseEntity<Void> budgetDecision(@PathVariable UUID id, @RequestBody BudgetApprovalRequest request) {
        controller.processBudgetDecision(id, request);
        return ResponseEntity.noContent().build();
    }
}
