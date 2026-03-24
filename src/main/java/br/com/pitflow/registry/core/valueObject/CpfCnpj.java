package br.com.pitflow.registry.core.valueObject;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;

import java.util.Objects;

public record CpfCnpj(String value) {
    private static final CPFValidator cpfValidator = new CPFValidator();
    private static final CNPJValidator cnpjValidator = new CNPJValidator();

    public CpfCnpj {
        Objects.requireNonNull(value, "O documento não pode ser nulo.");

        // Remove caracteres não numéricos para garantir a padronização
        String sanitizedValue = value.replaceAll("\\D", "");

        if (!isValid(sanitizedValue)) {
            throw new IllegalArgumentException("Invalid CPF or CNPJ " + value);
        }

        value = sanitizedValue;
    }

    private static boolean isValid(String doc) {
        if (doc.length() != 11 && doc.length() != 14) {
            return false;
        }

        // Verifica se todos os dígitos são iguais (ex: 111.111.111-11), o que é inválido
        if (doc.matches("(\\d)\\1{10,13}")) {
            return false;
        }

        var isCPF = doc.length() == 11;
        try {
            if (isCPF) {
                cpfValidator.assertValid(doc);
                return true;
            }
            cnpjValidator.assertValid(doc);
            return true;
        } catch (InvalidStateException e) {
            return false;
        }
    }


    public String getFormatted() {
        if (value.length() == 11) {
            return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return value.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
