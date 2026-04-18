package br.com.pitflow.registry.core.valueObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Email (String value) {
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public Email {
        if (value == null) {
            throw new IllegalArgumentException("O e-mail não pode ser nulo.");
        }

        Matcher matcher = EMAIL_PATTERN.matcher(value);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("O formato do e-mail é inválido: " + value);
        }
    }
}
