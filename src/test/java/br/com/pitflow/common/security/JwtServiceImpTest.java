package br.com.pitflow.common.security;

import br.com.pitflow.common.infrastructure.security.JwtServiceImp;
import br.com.pitflow.registry.core.entity.Mechanic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceImpTest {

    private JwtServiceImp jwtService;

    private final String secret = "364161b203a948480795c643b01859942a0094072f883b27b407a51801311099";
    private final Integer expirationHours = 1;

    @BeforeEach
    void setUp() {
        this.jwtService = new JwtServiceImp(secret, expirationHours);
    }

    @Test
    @DisplayName("Should generate a non-blank token string for a valid mechanic")
    void shouldGenerateToken() {
        // Arrange
        var mechanic = mock(Mechanic.class);
        when(mechanic.getUsername()).thenReturn("mechanic.test");
        when(mechanic.getName()).thenReturn("Test Mechanic");
        when(mechanic.getRole()).thenReturn("ROLE_MECHANIC");

        // Act
        String token = jwtService.generateToken(mechanic);

        // Assert
        assertThat(token).isNotBlank();
        // Um JWT possui 3 partes separadas por pontos: Header, Payload e Signature
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should validate a valid token and return the correct subject (username)")
    void shouldValidateValidToken() {
        // Arrange
        var mechanic = mock(Mechanic.class);
        when(mechanic.getUsername()).thenReturn("mechanic.admin");
        when(mechanic.getName()).thenReturn("Admin");
        when(mechanic.getRole()).thenReturn("ROLE_MECHANIC");

        String token = jwtService.generateToken(mechanic);

        // Act
        String subject = jwtService.validateToken(token);

        // Assert
        assertThat(subject).isEqualTo("mechanic.admin");
    }

    @Test
    @DisplayName("Should return null when the token is malformed or invalid")
    void shouldReturnNullForInvalidToken() {
        // Arrange
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.invalid-signature";

        // Act
        String subject = jwtService.validateToken(invalidToken);

        // Assert
        assertThat(subject).isNull();
    }
}