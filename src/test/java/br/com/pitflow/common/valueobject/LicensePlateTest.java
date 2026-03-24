package br.com.pitflow.common.valueobject;

import br.com.pitflow.registry.core.valueObject.LicensePlate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LicensePlateTest {
    @Test
    @DisplayName("Should create a valid traditional license plate")
    void shouldCreateValidTraditionalPlate() {
        var plate = new LicensePlate("ABC-1234");
        assertThat(plate.value()).isEqualTo("ABC1234");
    }

    @Test
    @DisplayName("Should create a valid Mercosul license plate")
    void shouldCreateValidMercosulPlate() {
        var plate = new LicensePlate("ABC1D23");
        assertThat(plate.value()).isEqualTo("ABC1D23");
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC-123", "ABC-12345", "123-ABCD", ""})
    @DisplayName("Should throw exception for invalid license plates")
    void shouldThrowExceptionForInvalidPlates(String invalidPlate) {
        assertThatThrownBy(() -> new LicensePlate(invalidPlate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should format traditional plate with hyphen")
    void shouldFormatTraditionalPlate() {
        var plate = new LicensePlate("ABC1234");
        assertThat(plate.getFormatted()).isEqualTo("ABC-1234");
    }
}
