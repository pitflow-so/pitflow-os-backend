package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import br.com.pitflow.operation.core.usecase.inputPort.CompleteDiagnosis;
import br.com.pitflow.registry.core.valueObject.Email;

import java.util.Map;
import java.util.UUID;

public class CompleteDiagnosisImp implements CompleteDiagnosis {

    private final ServiceOrderGateway repository;
    private final NotificationGateway notificationGateway;
    private final TokenGateway tokenGateway;
    private static final String APPROVED_DECISION = "APPROVED";
    private static final String REJECTED_DECISION = "REJECTED";
    private final String apiURL;

    public CompleteDiagnosisImp(
            ServiceOrderGateway repository,
            NotificationGateway notificationGateway,
            TokenGateway tokenGateway,
            String apiURL
    ) {
        this.repository = repository;
        this.notificationGateway = notificationGateway;
        this.tokenGateway = tokenGateway;
        this.apiURL = apiURL;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        var emailAddress = repository.findEmail(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Email to notification not found with OS ID: " + orderId));

        os.completeDiagnosis();
        repository.save(os);

        var approveToken = getTokenToMessage(os.getId().toString(), APPROVED_DECISION);
        var rejectToken = getTokenToMessage(os.getId().toString(), REJECTED_DECISION);


        String message = makeMessage(approveToken, rejectToken, os);

        var email = new Email(emailAddress);
        var notification = new Notification(message, email);
        notificationGateway.send(os.getId(), notification);
    }

    @Override
    public String getApiURl(){
        return this.apiURL;
    }

    private String makeMessage(String approveToken, String rejectToken, ServiceOrder os) {
        String baseUrl = this.apiURL;

        String approveLink = baseUrl + "?token=" + approveToken;
        String rejectLink = baseUrl + "?token=" + rejectToken;

        return String.format("""
                Seu orçamento para a OS %s está pronto.
                Valor total: R$ %s.

                Aprovar:
                %s

                Rejeitar:
                %s
                """,
                os.getId(),
                os.getTotalAmount(),
                approveLink,
                rejectLink
        );
    }

    private String getTokenToMessage(String osId, String decision){
        Map<String, Object> claims = Map.of(
            "serviceOrderId",osId,
            "status", decision
        );

        return tokenGateway.generateToken("external-decision", claims);
    }
}