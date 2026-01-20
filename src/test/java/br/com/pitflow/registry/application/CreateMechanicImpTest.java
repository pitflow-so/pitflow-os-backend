package br.com.pitflow.registry.application;

import br.com.pitflow.registry.application.dto.CreateMechanicDto;
import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class CreateMechanicImpTest {

    private MechanicRepository repository;
    private PasswordEncoder passwordEncoder;
    private CreateMechanicImp createMechanic;

    @BeforeEach
    void setUp() {
        // Inicialização manual dos mocks
        this.repository = mock(MechanicRepository.class);
        this.passwordEncoder = mock(PasswordEncoder.class);

        // Instanciação direta da classe sob teste
        this.createMechanic = new CreateMechanicImp(repository, passwordEncoder);
    }

    @Test
    @DisplayName("Should create mechanic successfully when username is unique")
    void shouldCreateMechanicSuccessfully() {
        // Arrange
        var dto = new CreateMechanicDto("Mestre do Torquimetro", "mestre.os", "password123");

        when(repository.findByUsername("mestre.os")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        // Act
        var result = createMechanic.execute(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("mestre.os");
        assertThat(result.getPassword()).isEqualTo("hashed_password");

        verify(repository).findByUsername("mestre.os");
        verify(passwordEncoder).encode("password123");
        verify(repository).save(any(Mechanic.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Arrange
        var dto = new CreateMechanicDto("Outro Nome", "mestre.os", "any_pass");
        var existingMechanic = new Mechanic("João", "mestre.os", "pass");

        when(repository.findByUsername("mestre.os")).thenReturn(Optional.of(existingMechanic));

        // Act & Assert
        assertThatThrownBy(() -> createMechanic.execute(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mechanic with username mestre.os already exists");

        verify(repository).findByUsername("mestre.os");
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).save(any(Mechanic.class));
    }
}