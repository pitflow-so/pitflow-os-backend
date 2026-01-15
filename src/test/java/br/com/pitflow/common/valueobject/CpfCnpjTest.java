package br.com.pitflow.common.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CpfCnpjTest {

    @Test
    @DisplayName("Should create a valid CPF and remove formatting")
    void shouldCreateValidCpfAndSanitize() {
        var cpf = new CpfCnpj("123.456.789-00");
        assertThat(cpf.value()).isEqualTo("12345678900");
    }

    @Test
    @DisplayName("Should create a valid CNPJ")
    void shouldCreateValidCnpj() {
        var cnpj = new CpfCnpj("12.345.678/0001-99");
        assertThat(cnpj.value()).isEqualTo("12345678000199");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "123456789012", "11111111111", ""})
    @DisplayName("Should throw exception for invalid documents")
    void shouldThrowExceptionForInvalidDocuments(String invalidDoc) {
        assertThatThrownBy(() -> new CpfCnpj(invalidDoc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid CPF or CNPJ");
    }

    @Test
    @DisplayName("Should throw exception for null value")
    void shouldThrowExceptionForNull() {
        assertThatThrownBy(() -> new CpfCnpj(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should format CPF correctly")
    void shouldFormatCpf() {
        var cpf = new CpfCnpj("12345678900");
        assertThat(cpf.getFormatted()).isEqualTo("123.456.789-00");
    }

    @Test
    @DisplayName("Should format CNPJ correctly")
    void shouldFormatCnpj() {
        var cnpj = new CpfCnpj("12345678000199");
        assertThat(cnpj.getFormatted()).isEqualTo("12.345.678/0001-99");
    }
}
