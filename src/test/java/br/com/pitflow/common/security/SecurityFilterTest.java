package br.com.pitflow.common.security;

import br.com.pitflow.common.infrastructure.security.JwtService;
import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private JwtService jwtService;
    private MechanicRepository mechanicRepository;
    private SecurityFilter securityFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        this.jwtService = mock(JwtService.class);
        this.mechanicRepository = mock(MechanicRepository.class);
        this.securityFilter = new SecurityFilter(jwtService, mechanicRepository);

        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
        this.filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate when a valid token is provided")
    void shouldAuthenticateWithValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid-token";
        String username = "teixeira";
        var mechanic = new Mechanic("Jorge", username, "encoded-pass");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(username);
        when(mechanicRepository.findByUsername(username)).thenReturn(Optional.of(mechanic));

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(username);

        //verify
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when token is missing")
    void shouldNotAuthenticateWhenTokenMissing() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        //verify
        verify(filterChain).doFilter(request, response);
    }
}