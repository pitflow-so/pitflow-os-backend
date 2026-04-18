package br.com.pitflow.common.valueobject;

import br.com.pitflow.registry.core.valueObject.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EmailTest {

    @Test
    @DisplayName("Should create valid email")
    void shouldCreateValidEmail() {
        var dummyEmail = "dummy@email.com";
        Email email = new Email(dummyEmail);
        assertThat(email.value()).isEqualTo(dummyEmail);
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void ShouldThrowExceptionWhenEmailIsNull() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O e-mail não pode ser nulo.");
    }

    @Test
    @DisplayName("Should throw exception when email is invalid")
    void ShouldThrowExceptionWhenInvalidEmail() {
        var dummyInvalidEmail = "rafael-gmail.com";
        assertThatThrownBy(() -> new Email(dummyInvalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O formato do e-mail é inválido: " + dummyInvalidEmail);
    }
}
