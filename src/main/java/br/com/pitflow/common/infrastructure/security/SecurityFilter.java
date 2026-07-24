package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.common.core.gateway.TokenGateway;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    private static final String CUSTOMER_SUBJECT_PREFIX = "customer:";
    private static final String CUSTOMER_ROLE = "ROLE_CUSTOMER";
    private static final String MECHANIC_ROLE = "ROLE_MECHANIC";

    private final TokenGateway tokenGateway;

    public SecurityFilter(TokenGateway tokenGateway) {
        this.tokenGateway = tokenGateway;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var token = recoverToken(request);

        if (token != null) {
            try {
                var subject = tokenGateway.validateToken(token);
                if (subject != null) {
                    authenticateUser(token, subject);
                    LOGGER.debug("Request URI: {}", request.getRequestURI());
                }
            } catch (Exception exception) {
                LOGGER.error("Error in token validation: {}", exception.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token, String subject) {
        var role = (String) tokenGateway.getClaims(token).get("role");

        if (!CUSTOMER_ROLE.equals(role) && !MECHANIC_ROLE.equals(role)) {
            LOGGER.warn("Token with unsupported role for subject: {}", subject);
            return;
        }

        String principal = subject.startsWith(CUSTOMER_SUBJECT_PREFIX)
                ? subject.substring(CUSTOMER_SUBJECT_PREFIX.length())
                : subject;

        var userDetails = User.builder()
                .username(principal)
                .password("")
                .authorities(role)
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.debug("Authorized subject {} with role {}", principal, role);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring("Bearer ".length());
    }
}
