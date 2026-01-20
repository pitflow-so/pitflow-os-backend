package br.com.pitflow.common.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor: ",
                ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(BadCredentialsException ex) {
        return buildProblemDetail(
                HttpStatus.UNAUTHORIZED,
                "Acesso Negado",
                "Usuário ou senha inválidos.");
    }

    private ResponseEntity<ProblemDetail> buildProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(status).body(pd);
    }
}
