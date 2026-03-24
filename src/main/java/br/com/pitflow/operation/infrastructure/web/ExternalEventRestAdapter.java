package br.com.pitflow.operation.infrastructure.web;

import br.com.pitflow.operation.controller.ServiceOrderController;
import br.com.pitflow.operation.infrastructure.web.dto.ExternalStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/events/service-orders")
public class ExternalEventRestAdapter {

    private final ServiceOrderController controller;

    public ExternalEventRestAdapter(ServiceOrderController controller) {
        this.controller = controller;
    }

    @PatchMapping("/status-update")
    @Operation(
            summary = "Webhook para atualização de status da OS",
            description = "Recebe eventos externos (ex: APPROVED, REJECTED, FINISHED) e atualiza o status da Ordem de Serviço conforme as regras de negócio."
    )
    public ResponseEntity<Void> updateStatus(@RequestBody ExternalStatusUpdateRequest request) {
        controller.processExternalStatusUpdate(request);
        return ResponseEntity.noContent().build();
    }
}
