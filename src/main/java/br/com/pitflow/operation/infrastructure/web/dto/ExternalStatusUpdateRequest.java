package br.com.pitflow.operation.infrastructure.web.dto;

import br.com.pitflow.operation.core.enums.ExternalStatusEvent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Evento externo para atualização de status da Ordem de Serviço")
public record ExternalStatusUpdateRequest(

        @Schema(description = "ID da Ordem de Serviço", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID serviceOrderId,

        @Schema(
                description = "Evento que define a ação a ser executada na OS. Valores suportados: APPROVED, REJECTED, FINISHED",
                example = "APPROVED"
        )
        ExternalStatusEvent event,

        @Schema(
                description = "Motivo da ação (obrigatório para eventos de cancelamento/rejeição)",
                example = "Cliente recusou o orçamento"
        )
        String reason
) {
}