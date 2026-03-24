package br.com.pitflow.registry.core.valueObject;

import java.util.Objects;

public record LicensePlate(String value) {

    public LicensePlate {
        Objects.requireNonNull(value, "License plate cannot be null.");

        String sanitized = value.toUpperCase().replaceAll("[^A-Z0-9]", "");

        if (!isValid(sanitized)) {
            throw new IllegalArgumentException("Invalid license plate format: " + value);
        }

        value = sanitized;
    }

    private static boolean isValid(String plate) {
        // RegEx for Traditional (3 letters + 4 numbers) or Mercosul (3 letters + 1 number + 1 letter + 2 numbers)
        return plate.matches("[A-Z]{3}[0-9]{4}") || plate.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");
    }

    public String getFormatted() {
        // Returns formatted as AAA-9999 for traditional or keeps Mercosul as is
        if (value.matches("[A-Z]{3}[0-9]{4}")) {
            return value.substring(0, 3) + "-" + value.substring(3);
        }
        return value;
    }
}
