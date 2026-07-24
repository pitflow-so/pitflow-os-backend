package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.common.core.gateway.TokenGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityFilterTest {

    private TokenGateway tokenGateway;
    private SecurityFilter securityFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        tokenGateway = mock(TokenGateway.class);
        securityFilter = new SecurityFilter(tokenGateway);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate a mechanic from signed token claims")
    void shouldAuthenticateMechanicWithValidToken() throws ServletException, IOException {
        String token = "valid-token";
        String username = "teixeira";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenGateway.validateToken(token)).thenReturn(username);
        when(tokenGateway.getClaims(token)).thenReturn(Map.of("role", "ROLE_MECHANIC"));

        securityFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(username);
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_MECHANIC");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should authenticate a customer and normalize its subject")
    void shouldAuthenticateCustomerWithValidToken() throws ServletException, IOException {
        String token = "valid-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenGateway.validateToken(token)).thenReturn("customer:customer-id");
        when(tokenGateway.getClaims(token)).thenReturn(Map.of("role", "ROLE_CUSTOMER"));

        securityFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("customer-id");
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_CUSTOMER");
    }

    @Test
    @DisplayName("Should not authenticate a token with an unsupported role")
    void shouldNotAuthenticateUnsupportedRole() throws ServletException, IOException {
        String token = "valid-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenGateway.validateToken(token)).thenReturn("external-decision");
        when(tokenGateway.getClaims(token)).thenReturn(Map.of("status", "APPROVED"));

        securityFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when token is missing")
    void shouldNotAuthenticateWhenTokenMissing() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
