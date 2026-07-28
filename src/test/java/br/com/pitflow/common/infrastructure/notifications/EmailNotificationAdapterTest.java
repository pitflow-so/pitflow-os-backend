package br.com.pitflow.common.infrastructure.notifications;

import br.com.pitflow.operation.core.gateway.dto.Notification;
import br.com.pitflow.operation.core.valueobject.Email;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailNotificationAdapterTest {
    @Test
    void buildsAndSendsMimeMessage() {
        var sender = mock(JavaMailSender.class);
        var message = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(message);
        var adapter = new EmailNotificationAdapter(sender, "noreply@pitflow.local");

        adapter.send(UUID.randomUUID(), new Notification(
                "Pagamento", "<b>Link</b>", new Email("cliente@example.com")));

        verify(sender).send(message);
    }

    @Test
    void wrapsMailFailures() {
        var sender = mock(JavaMailSender.class);
        when(sender.createMimeMessage()).thenThrow(new IllegalStateException("SMTP down"));
        var adapter = new EmailNotificationAdapter(sender, "noreply@pitflow.local");
        assertThrows(IllegalStateException.class, () -> adapter.send(UUID.randomUUID(),
                new Notification("Assunto", "Mensagem", new Email("cliente@example.com"))));
    }
}
