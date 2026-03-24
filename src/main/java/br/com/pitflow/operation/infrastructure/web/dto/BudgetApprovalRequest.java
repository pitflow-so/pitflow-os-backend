package br.com.pitflow.operation.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aprovar ou recusar o orçamento da Ordem de Serviço")
public record BudgetApprovalRequest(
        @Schema(description = "Status da aprovação true (Aprovado) ou false (Recusada)", example = "false")
        boolean approved,

        @Schema(description = "Motivo em caso de recusado, pode ser vazio caso aprovado", example = "Não está dentro do meu orçamento")
        String reason
) {}