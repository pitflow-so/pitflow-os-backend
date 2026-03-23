package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.controller.PartController;
import br.com.pitflow.inventory.infrastructure.web.dto.CreatePartRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdatePartRequest;
import br.com.pitflow.inventory.presenter.dto.PartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory/parts")
@Tag(name = "Inventory - Parts", description = "Gerenciamento de peças e componentes")
public class PartRestAdapter {

    private final PartController controller;

    public PartRestAdapter(
            PartController controller
    ) {
        this.controller = controller;
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Cadastrar nova peça", description = "Adiciona uma peça ao inventário com SKU único.")
    public ResponseEntity<PartResponse> create(@RequestBody CreatePartRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(controller.create(dto));
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar peça por ID")
    public ResponseEntity<PartResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(controller.findPartById(id));
    }

    @GetMapping("/sku/{sku}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar peça po SKU")
    public ResponseEntity<PartResponse> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(controller.findPartBySku(sku));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar todas as peças")
    public ResponseEntity<List<PartResponse>> listAll() {
        //TODO: Change to Page<PartResponse> in the future
        return ResponseEntity.ok(controller.listParts());
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar peça", description = "Atualiza os dados de uma peça existente. Se o SKU for alterado, valida a unicidade.")
    public ResponseEntity<PartResponse> update(@PathVariable UUID id, @RequestBody UpdatePartRequest dto) {
        return ResponseEntity.ok(controller.updatePart(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Excluir peça", description = "Remove permanentemente uma peça do inventário.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        controller.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
