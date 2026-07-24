package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderGateway;
import br.com.pitflow.operation.core.gateway.ServiceOrderMetricsGateway;
import br.com.pitflow.operation.core.gateway.RegistryGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import br.com.pitflow.operation.core.valueobject.Email;
import br.com.pitflow.operation.core.usecase.inputPort.CompleteDiagnosis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CompleteDiagnosisImp implements CompleteDiagnosis {

    private final ServiceOrderGateway repository;
    private final NotificationGateway notificationGateway;
    private final ServiceOrderMetricsGateway metricsGateway;
    private final TokenGateway tokenGateway;
    private final RegistryGateway registryGateway;
    private static final String APPROVED_DECISION = "APPROVED";
    private static final String REJECTED_DECISION = "REJECTED";
    private static final String PATH_DECISION = "/external/events/service-orders/decision";
    private final String apiURL;

    public CompleteDiagnosisImp(
            ServiceOrderGateway repository,
            NotificationGateway notificationGateway,
            ServiceOrderMetricsGateway metricsGateway,
            TokenGateway tokenGateway,
            RegistryGateway registryGateway,
            String apiURL
    ) {
        this.repository = repository;
        this.notificationGateway = notificationGateway;
        this.metricsGateway = metricsGateway;
        this.tokenGateway = tokenGateway;
        this.registryGateway = registryGateway;
        this.apiURL = apiURL;
    }

    @Override
    public void execute(UUID orderId) {
        var os = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service Order not found with ID: " + orderId));

        var emailAddress = registryGateway.findCustomerEmail(os.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Email to notification not found with OS ID: " + orderId));

        String previousStatus = os.getStatus().name();
        Duration timeSpentToCompleteDiagnosis = Duration.between(os.getDiagnosisStartedAt(), LocalDateTime.now());

        os.completeDiagnosis();
        repository.save(os);

        metricsGateway.recordTimeInStatus(previousStatus, timeSpentToCompleteDiagnosis);

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
        String approveLink = this.apiURL + PATH_DECISION + "?token=" + approveToken;
        String rejectLink  = this.apiURL + PATH_DECISION + "?token=" + rejectToken;

        return layoutEmail(approveLink, rejectLink, os);
    }

    private String getTokenToMessage(String osId, String decision){
        Map<String, Object> claims = Map.of(
            "serviceOrderId",osId,
            "status", decision
        );

        return tokenGateway.generateToken("external-decision", claims);
    }

    private String layoutEmail(String aprove, String reject,  ServiceOrder os){
        return String.format("""
        <!DOCTYPE html>
        <html lang="pt-BR">
        <body style="margin:0;padding:0;background:#f4f4f4;font-family:Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding:40px 0;">
                <table width="600" cellpadding="0" cellspacing="0"
                       style="background:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);">

                  <!-- Header -->
                  <tr>
                    <td style="background:#1a1a2e;padding:32px;text-align:center;">
                      <h1 style="margin:0;color:#ffffff;font-size:24px;letter-spacing:1px;">PITFLOW</h1>
                      <p style="margin:6px 0 0;color:#a0a0c0;font-size:13px;">Gestão de Ordens de Serviço</p>
                    </td>
                  </tr>

                  <!-- Body -->
                  <tr>
                    <td style="padding:40px 48px;">
                      <h2 style="margin:0 0 8px;color:#1a1a2e;font-size:20px;">Orçamento disponível</h2>
                      <p style="margin:0 0 24px;color:#666;font-size:14px;">
                        O diagnóstico da sua ordem de serviço foi concluído e o orçamento está pronto para sua aprovação.
                      </p>

                      <!-- OS Info -->
                      <table width="100%%" cellpadding="0" cellspacing="0"
                             style="background:#f8f8fb;border-radius:6px;margin-bottom:32px;">
                        <tr>
                          <td style="padding:16px 20px;">
                            <p style="margin:0 0 6px;color:#999;font-size:12px;text-transform:uppercase;letter-spacing:1px;">
                              Ordem de Serviço
                            </p>
                            <p style="margin:0;color:#1a1a2e;font-size:13px;font-family:monospace;">%s</p>
                          </td>
                          <td style="padding:16px 20px;text-align:right;">
                            <p style="margin:0 0 6px;color:#999;font-size:12px;text-transform:uppercase;letter-spacing:1px;">
                              Valor Total
                            </p>
                            <p style="margin:0;color:#1a1a2e;font-size:22px;font-weight:bold;">R$ %s</p>
                          </td>
                        </tr>
                      </table>

                      <p style="margin:0 0 20px;color:#444;font-size:14px;text-align:center;">
                        Escolha uma das opções abaixo para prosseguir:
                      </p>

                      <!-- Buttons -->
                      <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                          <td align="center" style="padding:0 8px 0 0;">
                            <a href="%s"
                               style="display:block;background:#2e7d32;color:#ffffff;text-decoration:none;
                                      padding:16px 0;border-radius:6px;font-size:16px;font-weight:bold;
                                      text-align:center;">
                              ✔ Aprovar Orçamento
                            </a>
                          </td>
                          <td align="center" style="padding:0 0 0 8px;">
                            <a href="%s"
                               style="display:block;background:#c62828;color:#ffffff;text-decoration:none;
                                      padding:16px 0;border-radius:6px;font-size:16px;font-weight:bold;
                                      text-align:center;">
                              ✘ Rejeitar Orçamento
                            </a>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>

                  <!-- Footer -->
                  <tr>
                    <td style="background:#f8f8fb;padding:24px 48px;text-align:center;
                               border-top:1px solid #eeeeee;">
                      <p style="margin:0;color:#999;font-size:12px;">
                        Este link expira em <strong>3 horas</strong>. Caso tenha dúvidas, entre em contato com a oficina.
                      </p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """,
                os.getId(),
                os.getTotalAmount(),
                aprove,
                reject
        );
    }
}
