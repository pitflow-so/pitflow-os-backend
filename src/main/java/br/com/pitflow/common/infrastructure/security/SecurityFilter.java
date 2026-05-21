package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SecurityFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    private final TokenGateway tokenGateway;
    private final MechanicGateway mechanicGateway;

    // Prefixo para identificar tokens de cliente (lambda)
    private static final String CUSTOMER_SUBJECT_PREFIX = "customer:";
    private static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";

    public SecurityFilter(TokenGateway tokenGateway, MechanicGateway mechanicGateway) {
        this.tokenGateway = tokenGateway;
        this.mechanicGateway = mechanicGateway;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null) {
            try {
                var subject = tokenGateway.validateToken(token);

                if (subject != null) {
                    authenticateUser(token, subject);
                    logger.debug("Request URI: {}", request.getRequestURI());
                }
            } catch (Exception e) {
                logger.error("Error in token validation: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token, String subject) {
        // Estratégia 1: Verificar por prefixo no subject
        if (subject.startsWith(CUSTOMER_SUBJECT_PREFIX)) {
            authenticateAsCustomer(subject);
            return;
        }

        // Estratégia 2: Verificar claims do token
        var claims = tokenGateway.getClaims(token);
        var role = (String) claims.get("role");

        if (CUSTOMER_ROLE.equals(role)) {
            authenticateAsCustomer(subject);
            return;
        }

        // Estratégia 3: Assumir que é mecânico e buscar no banco (apenas se necessário)
        authenticateAsMechanic(subject);
    }

    private void authenticateAsCustomer(String subject) {
        // Remove o prefixo se existir
        String customerId = subject.replace(CUSTOMER_SUBJECT_PREFIX, "");

        var userDetails = User.builder()
                .username(customerId)
                .password("")
                .authorities(CUSTOMER_ROLE)
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Authorized customer: {}", customerId);
    }

    private void authenticateAsMechanic(String username) {
        var mechanic = mechanicGateway.findByUsername(username);

        if (mechanic.isPresent()) {
            var mechanicData = mechanic.get();
            var userDetails = User.builder()
                    .username(mechanicData.getUsername())
                    .password(mechanicData.getPassword())
                    .authorities(mechanicData.getRole())
                    .build();

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Authorized Mechanic: {}", username);
        } else {
            logger.warn("Mechanic not found: {}", username);
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}