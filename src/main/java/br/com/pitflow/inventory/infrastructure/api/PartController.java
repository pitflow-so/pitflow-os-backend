package br.com.pitflow.inventory.infrastructure.api;

import br.com.pitflow.inventory.application.dto.CreatePartDto;
import br.com.pitflow.inventory.application.dto.UpdatePartDto;
import br.com.pitflow.inventory.application.usecase.CreatePart;
import br.com.pitflow.inventory.application.usecase.DeletePart;
import br.com.pitflow.inventory.application.usecase.FindPartById;
import br.com.pitflow.inventory.application.usecase.FindPartBySku;
import br.com.pitflow.inventory.application.usecase.ListParts;
import br.com.pitflow.inventory.application.usecase.UpdatePart;
import br.com.pitflow.inventory.domain.Part;
import br.com.pitflow.inventory.infrastructure.api.dto.PartResponse;
import io.swagger.v3.oas.annotations.Operation;
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
public class PartController {

    private final CreatePart createPart;
    private final FindPartById findPartById;
    private final FindPartBySku findPartBySku;
    private final ListParts listParts;
    private final UpdatePart updatePart;
    private final DeletePart deletePart;

    public PartController(
            CreatePart createPart,
            FindPartById findPartById,
            FindPartBySku findPartBySku,
            ListParts listParts,
            UpdatePart updatePart,
            DeletePart deletePart
    ) {
        this.createPart = createPart;
        this.findPartById = findPartById;
        this.findPartBySku = findPartBySku;
        this.listParts = listParts;
        this.updatePart = updatePart;
        this.deletePart = deletePart;
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova peça", description = "Adiciona uma peça ao inventário com SKU único.")
    public ResponseEntity<PartResponse> create(@RequestBody CreatePartDto dto) {
        var part = createPart.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(part));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar peça por ID")
    public ResponseEntity<PartResponse> getById(@PathVariable UUID id) {
        var part = findPartById.execute(id);
        return ResponseEntity.ok(toResponse(part));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Buscar peça po SKU")
    public ResponseEntity<PartResponse> getBySku(@PathVariable String sku) {
        var part = findPartBySku.execute(sku);
        return ResponseEntity.ok(toResponse(part));
    }

    @GetMapping
    @Operation(summary = "Listar todas as peças")
    public ResponseEntity<List<PartResponse>> listAll() {
        var parts = listParts.execute();
        var response = parts.stream().map(this::toResponse).toList();
        //TODO: Change to Page<PartResponse> in the future
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar peça", description = "Atualiza os dados de uma peça existente. Se o SKU for alterado, valida a unicidade.")
    public ResponseEntity<PartResponse> update(@PathVariable UUID id, @RequestBody UpdatePartDto dto) {
        updatePart.execute(id, dto);
        var updatedPart = findPartById.execute(id);
        return ResponseEntity.ok(toResponse(updatedPart));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir peça", description = "Remove permanentemente uma peça do inventário.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deletePart.execute(id);
        return ResponseEntity.noContent().build();
    }

    private PartResponse toResponse(Part part) {
        return new PartResponse(part.getId(), part.getSku(), part.getName(),
                part.getDescription(), part.getPrice(), part.getStockQuantity());
    }
}
