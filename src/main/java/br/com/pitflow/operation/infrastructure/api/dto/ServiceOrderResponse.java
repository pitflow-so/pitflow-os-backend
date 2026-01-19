package br.com.pitflow.operation.infrastructure.api.dto;

import br.com.pitflow.operation.domain.ServiceOrder.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Representação completa de uma Ordem de Serviço para resposta da API")
public record ServiceOrderResponse(
        @Schema(description = "ID único da Ordem de Serviço", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID do cliente proprietário", example = "a2d1b3c4-e5f6-7890-abcd-1234567890ab")
        UUID customerId,

        @Schema(description = "ID do veículo relacionado", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID vehicleId,

        @Schema(description = "Relato inicial do cliente sobre o problema", example = "Barulho metálico na suspensão dianteira ao passar em buracos")
        String description,

        @Schema(description = "Status atual da OS no workflow", example = "IN_DIAGNOSIS")
        Status status,

        @Schema(description = "Valor total acumulado (peças + serviços)", example = "450.50")
        BigDecimal totalAmount,

        @Schema(description = "Data e hora de abertura da OS", example = "2026-01-18T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora de finalização técnica", example = "2026-01-18T15:30:00")
        LocalDateTime finishedAt,

        @Schema(description = "Lista de itens (peças e serviços) incluídos no orçamento")
        List<ServiceOrderItemResponse> items,

        @Schema(description = "Motivo do cancelamento (se aplicável)", example = "Cliente optou por realizar o serviço em outra data")
        String cancellationDescription
) {}