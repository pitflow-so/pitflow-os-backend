package br.com.pitflow.registry.infrastructure.api;

import br.com.pitflow.registry.application.dto.LoginDto;
import br.com.pitflow.registry.application.usecases.AuthenticateMechanic;
import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.infrastructure.api.dto.AuthenticationResponse;
import br.com.pitflow.registry.infrastructure.api.dto.MechanicResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registry/auth")
@Tag(name = "Registry - Authentication", description = "Endpoint para login de mecânicos")
public class AuthController {

    private final AuthenticateMechanic authenticateMechanic;

    public AuthController(AuthenticateMechanic authenticateMechanic) {
        this.authenticateMechanic = authenticateMechanic;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginDto dto) {
        // 1. Executa a lógica de autenticação
        var result = authenticateMechanic.execute(dto);

        // 2. Mapeia para a resposta da API
        var response = new AuthenticationResponse(
                result.token(),
                toMechanicResponse(result.mechanic())
        );

        return ResponseEntity.ok(response);
    }

    private MechanicResponse toMechanicResponse(Mechanic mechanic) {
        return new MechanicResponse(
                mechanic.getId(),
                mechanic.getName(),
                mechanic.getUsername()
        );
    }
}