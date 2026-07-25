package br.com.pitflow.operation.infrastructure.web;

import br.com.pitflow.operation.controller.ExternalDecisionController;
import br.com.pitflow.operation.infrastructure.web.dto.ExternalDecisionRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/events/service-orders")
public class ExternalEventRestAdapter {

    private final ExternalDecisionController externalDecisionController;

    public ExternalEventRestAdapter(
            ExternalDecisionController externalDecisionController
    ) {
        this.externalDecisionController = externalDecisionController;
    }

    @PostMapping("/decision")
    @Operation(
            summary = "Confirma a decisão de orçamento recebida pelo formulário externo",
            description = "Valida o token assinado, registra aprovação ou recusa e aceita o motivo da recusa."
    )
    public ResponseEntity<Void> confirmDecision(@RequestBody ExternalDecisionRequest request) {
        externalDecisionController.processDecision(request.token(), request.reason());
        return ResponseEntity.noContent().build();
    }

}
