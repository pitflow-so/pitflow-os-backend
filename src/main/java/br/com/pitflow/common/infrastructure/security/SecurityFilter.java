package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SecurityFilter extends OncePerRequestFilter {

    private final TokenGateway tokenGateway;
    private final MechanicGateway mechanicGateway;

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
            var username = tokenGateway.validateToken(token);

            if (username != null) {
                var mechanic = mechanicGateway.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Mechanic not found"));

                var userDetails = User.builder()
                        .username(mechanic.getUsername())
                        .password(mechanic.getPassword())
                        .authorities(mechanic.getRole())
                        .build();

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}