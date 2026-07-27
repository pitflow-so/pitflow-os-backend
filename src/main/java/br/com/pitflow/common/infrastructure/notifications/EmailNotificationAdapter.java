package br.com.pitflow.common.infrastructure.notifications;

import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.operation.core.gateway.dto.Notification;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.UUID;

public class EmailNotificationAdapter implements NotificationGateway {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(EmailNotificationAdapter.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotificationAdapter(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(
            UUID serviceOrderId,
            Notification notification
    ) {
        String to = notification.to().value();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(notification.subject());
            helper.setText(notification.message(), true);
            mailSender.send(mimeMessage);
            LOGGER.info(
                    "[EMAIL SENT] OS ID: {} - To: {}",
                    serviceOrderId,
                    to
            );
        } catch (Exception exception) {
            LOGGER.error(
                    "[EMAIL ERROR] OS ID: {} - Error: {}",
                    serviceOrderId,
                    exception.getMessage(),
                    exception
            );
            throw new IllegalStateException(
                    "Failed to send email",
                    exception
            );
        }
    }
}
