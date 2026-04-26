package br.com.pitflow.common.infrastructure.notifications;

import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

public class EmailNotificationAdapter implements NotificationGateway {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationAdapter.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotificationAdapter(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(UUID serviceOrderId, Notification notification) {

        String to = notification.to().value();

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(to);
            mail.setSubject("Pitflow - Orçamento da Ordem de Serviço");
            mail.setText(notification.message());

            mailSender.send(mail);

            logger.info("[EMAIL SENT] OS ID: {} - To: {}", serviceOrderId, to);

        } catch (Exception e) {
            logger.error("[EMAIL ERROR] OS ID: {} - Error: {}", serviceOrderId, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
