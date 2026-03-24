package br.com.pitflow.registry.infrastructure.web;

import br.com.pitflow.registry.infrastructure.web.dto.LoginRequest;
import br.com.pitflow.registry.controller.AuthController;
import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.presenter.dto.AuthenticationResponse;
import br.com.pitflow.registry.presenter.dto.MechanicResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registry/auth")
@Tag(name = "Registry - Authentication", description = "Endpoint para login de mecânicos")
public class AuthRestAdapter {

    private final AuthController authController;

    public AuthRestAdapter(AuthController authController) {
        this.authController = authController;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest dto) {
        return ResponseEntity.ok(authController.authenticate(dto));
    }
}