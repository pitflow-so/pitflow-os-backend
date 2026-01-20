package br.com.pitflow.registry.application;

import br.com.pitflow.common.infrastructure.security.JwtService;
import br.com.pitflow.registry.application.dto.LoginDto;
import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthenticateMechanicImpTest {

    private MechanicRepository repository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticateMechanicImp authenticateMechanic;

    @BeforeEach
    void setUp() {
        this.repository = mock(MechanicRepository.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.jwtService = mock(JwtService.class);
        this.authenticateMechanic = new AuthenticateMechanicImp(repository, passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Should authenticate successfully and return token")
    void shouldAuthenticateSuccessfully() {
        // Arrange
        var loginDto = new LoginDto("admin", "password123");
        var mechanic = new Mechanic("Mestre", "admin", "encoded_password");

        when(repository.findByUsername("admin")).thenReturn(Optional.of(mechanic));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(mechanic)).thenReturn("valid_jwt_token");

        // Act
        var result = authenticateMechanic.execute(loginDto);

        // Assert
        assertThat(result.token()).isEqualTo("valid_jwt_token");
        assertThat(result.mechanic().getUsername()).isEqualTo("admin");
        verify(jwtService).generateToken(mechanic);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password does not match")
    void shouldThrowExceptionWhenPasswordInvalid() {
        // Arrange
        var loginDto = new LoginDto("admin", "wrong_password");
        var mechanic = new Mechanic("Mestre", "admin", "encoded_password");

        when(repository.findByUsername("admin")).thenReturn(Optional.of(mechanic));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authenticateMechanic.execute(loginDto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(jwtService, never()).generateToken(any());
    }
}