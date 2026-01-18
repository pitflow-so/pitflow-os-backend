package br.com.pitflow.registry.infrastructure.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ControllerExceptionsHandler {

    @ExceptionHandler(IllegalStateException.class) // Ex: Cliente já existe
    public ResponseEntity<ProblemDetail> handleConflict(IllegalStateException ex) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Conflito de Dados", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class) // Ex: CPF inválido
    public ResponseEntity<ProblemDetail> handleBadRequest(IllegalArgumentException ex) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Dados Inválidos", ex.getMessage());
    }

    private ResponseEntity<ProblemDetail> buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(status).body(pd);
    }
}
