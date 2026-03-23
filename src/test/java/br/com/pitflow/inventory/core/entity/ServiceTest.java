package br.com.pitflow.inventory.core.entity;

import br.com.pitflow.inventory.core.entity.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ServiceTest {

    @Test
    @DisplayName("Should create a valid labor service")
    void shouldCreateService() {
        var service = new Service("Balanceamento de pneus", "Alinhamento e balanceamento das 4 rodas", new BigDecimal("150.00"));
        assertThat(service.getName()).isEqualTo("Balanceamento de pneus");
    }

    @Test
    @DisplayName("Should throw exception for negative service price")
    void shouldThrowExceptionForNegativePrice() {
        assertThatThrownBy(() -> new Service("Dummy", "Description", new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
