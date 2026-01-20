package br.com.pitflow.registry.infrastructure.api;

import br.com.pitflow.registry.application.dto.CreateMechanicDto;
import br.com.pitflow.registry.application.usecases.CreateMechanic;
import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.infrastructure.api.dto.MechanicResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registry/mechanics")
@Tag(name = "Registry - Mechanics", description = "Endpoint para cadastro dos mêcanicos")
public class MechanicController {
    private final CreateMechanic createMechanic;

    public MechanicController(CreateMechanic createMechanic) {
        this.createMechanic = createMechanic;
    }

    @PostMapping
    public ResponseEntity<MechanicResponse> create(@RequestBody CreateMechanicDto dto) {
        var mechanic = createMechanic.execute(dto);

        var response = toResponse(mechanic);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private MechanicResponse toResponse(Mechanic mechanic) {
        return new MechanicResponse(
                mechanic.getId(),
                mechanic.getName(),
                mechanic.getUsername()
        );
    }
}
