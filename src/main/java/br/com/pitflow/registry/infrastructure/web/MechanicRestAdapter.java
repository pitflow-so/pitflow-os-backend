package br.com.pitflow.registry.infrastructure.web;

import br.com.pitflow.registry.controller.MechanicController;
import br.com.pitflow.registry.infrastructure.web.dto.CreateMechanicRequest;
import br.com.pitflow.registry.presenter.dto.MechanicResponse;
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
public class MechanicRestAdapter {
    private final MechanicController mechanicController;

    public MechanicRestAdapter(MechanicController mechanicController) {
        this.mechanicController = mechanicController;
    }

    @PostMapping
    public ResponseEntity<MechanicResponse> create(@RequestBody CreateMechanicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mechanicController.create(request));
    }
}
