package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.infrastructure.web.dto.CreateServiceRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.UpdateServiceRequest;
import br.com.pitflow.inventory.controller.ServiceController;
import br.com.pitflow.inventory.core.entity.Service;
import br.com.pitflow.inventory.presenter.dto.ServiceResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/inventory/services")
@Tag(name = "Inventory - Services", description = "Endpoints para gestão do catálogo de serviços (Mão de Obra)")
public class ServiceRestAdapter {

    private final ServiceController controller;

    public ServiceRestAdapter(
            ServiceController controller
    ) {
        this.controller = controller;
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Criar um novo serviço", description = "Registra um serviço de mão de obra no catálogo de serviços da oficina.")
    public ResponseEntity<ServiceResponse> create(@RequestBody CreateServiceRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(controller.create(dto));
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar serviço por ID", description = "Recupera os detalhes de um serviço específico usando seu identificador único.")
    public ResponseEntity<ServiceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(controller.findById(id));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar todos os serviços", description = "Retorna uma lista de todos os serviços cadastrados no catálogo.")
    public ResponseEntity<java.util.List<ServiceResponse>> listAll() {
        //TODO: Implement pagination
        return ResponseEntity.ok(controller.findAll());
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar serviço", description = "Atualiza o nome, descrição ou preço de um serviço de mão de obra.")
    public ResponseEntity<ServiceResponse> update(@PathVariable UUID id, @RequestBody UpdateServiceRequest dto) {
        return ResponseEntity.ok(controller.update(id,dto));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Excluir serviço", description = "Remove um serviço do catálogo de mão de obra.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        controller.delete(id);
        return ResponseEntity.noContent().build();
    }

}
