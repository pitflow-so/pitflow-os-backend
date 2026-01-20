package br.com.pitflow.registry.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MechanicTest {

    @Test
    @DisplayName("Should create a valid mechanic with default role")
    void shouldCreateValidMechanic() {
        // Arrange & Act
        var mechanic = new Mechanic("Mestre Splinter", "splinter", "senha123");

        // Assert
        assertThat(mechanic.getId()).isNotNull();
        assertThat(mechanic.getRole()).isEqualTo("ROLE_MECHANIC");
        assertThat(mechanic.getUsername()).isEqualTo("splinter");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should throw exception when name is invalid")
    void shouldThrowExceptionWhenNameIsInvalid(String invalidName) {
        assertThatThrownBy(() -> new Mechanic(invalidName, "user", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name is required");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when username is invalid")
    void shouldThrowExceptionWhenUsernameIsInvalid(String invalidUser) {
        assertThatThrownBy(() -> new Mechanic("Name", invalidUser, "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is required");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when password is invalid")
    void shouldThrowExceptionWhenPasswordIsInvalid(String invalidPass) {
        assertThatThrownBy(() -> new Mechanic("Name", "user", invalidPass))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password is required");
    }
}