package br.com.pitflow.registry.core.usecase.mechanic;

import br.com.pitflow.common.core.gateway.PasswordVerifierGateway;
import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.registry.controller.dto.LoginCommand;
import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticateMechanicImpTest {

    private MechanicGateway mechanicGateway;
    private PasswordVerifierGateway passwordVerifier;
    private TokenGateway tokenGateway;
    private AuthenticateMechanicImp authenticateMechanic;

    @BeforeEach
    void setUp() {
        this.mechanicGateway = mock(MechanicGateway.class);
        this.passwordVerifier = mock(PasswordVerifierGateway.class);
        this.tokenGateway = mock(TokenGateway.class);
        this.authenticateMechanic = new AuthenticateMechanicImp(
                mechanicGateway, passwordVerifier, tokenGateway);
    }

    @Test
    @DisplayName("Should authenticate successfully and return token")
    void shouldAuthenticateSuccessfully() {
        // Arrange
        var command = new LoginCommand("admin", "password123");
        var mechanic = new Mechanic("Mestre", "admin", "encoded_password");

        when(mechanicGateway.findByUsername("admin")).thenReturn(Optional.of(mechanic));
        when(passwordVerifier.matches("password123", "encoded_password")).thenReturn(true);
        when(tokenGateway.generateToken(eq("admin"), any(Map.class))).thenReturn("valid_jwt_token");

        // Act
        var result = authenticateMechanic.execute(command);

        // Assert
        assertThat(result.token()).isEqualTo("valid_jwt_token");
        assertThat(result.mechanic().getUsername()).isEqualTo("admin");
        verify(tokenGateway).generateToken(eq("admin"), any(Map.class));
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void shouldThrowExceptionWhenPasswordInvalid() {
        // Arrange
        var command  = new LoginCommand("admin", "wrong_password");
        var mechanic = new Mechanic("Mestre", "admin", "encoded_password");

        when(mechanicGateway.findByUsername("admin")).thenReturn(Optional.of(mechanic));
        when(passwordVerifier.matches("wrong_password", "encoded_password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authenticateMechanic.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        verify(tokenGateway, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when mechanic not found")
    void shouldThrowExceptionWhenMechanicNotFound() {
        // Arrange
        var command = new LoginCommand("unknown", "password123");

        when(mechanicGateway.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authenticateMechanic.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        // Verify
        verify(tokenGateway, never()).generateToken(any(), any());
        verify(passwordVerifier, never()).matches(any(), any());
    }
}