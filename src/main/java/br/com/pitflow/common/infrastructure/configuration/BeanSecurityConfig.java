package br.com.pitflow.common.infrastructure.configuration;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.common.infrastructure.notifications.EmailNotificationAdapter;
import br.com.pitflow.common.infrastructure.notifications.LogNotificationAdapterMock;
import br.com.pitflow.common.infrastructure.security.JwtServiceImp;
import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import br.com.pitflow.common.infrastructure.transaction.SpringTransactionAdapter;
import br.com.pitflow.operation.core.gateway.NotificationGateway;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class BeanSecurityConfig {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private Integer expirationHours;

    @Bean
    public TokenGateway tokenGateway() {
        return new JwtServiceImp(secret, expirationHours);
    }

    @Bean
    public SecurityFilter securityFilter(TokenGateway tokenGateway, MechanicGateway mechanicGateway) {
        return new SecurityFilter(tokenGateway, mechanicGateway);
    }

    @Bean
    public TransactionGateway transactionGateway() {
        return new SpringTransactionAdapter();
    }


    @Bean
    public NotificationGateway notificationService(
            @Value("${mock.send-message}") boolean mockMessage,
            @Value("${spring.mail.username}")  String enterpriseEmail,
            JavaMailSender mailSender
    ) {
        if(mockMessage)
            return new LogNotificationAdapterMock();
        return new EmailNotificationAdapter(mailSender, enterpriseEmail);
    }
}