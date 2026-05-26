package br.com.pitflow.registry.infrastructure.web;

import br.com.pitflow.registry.controller.CustomerController;
import br.com.pitflow.registry.infrastructure.web.dto.CreateCustomerRequest;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateCustomerRequest;
import br.com.pitflow.registry.presenter.dto.CustomerResponse;
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
@RequestMapping("/registry/customers")
@Tag(name = "Registry - Customers", description = "Gerenciamento de clientes")
public class CustomerRestAdapter {

    private final CustomerController controller;

    public CustomerRestAdapter(CustomerController controller) {
        this.controller = controller;
    }

    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cria um novo cliente com os dados fornecidos.")
    public ResponseEntity<CustomerResponse> create(@RequestBody CreateCustomerRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(controller.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, @RequestBody UpdateCustomerRequest dto) {
        return ResponseEntity.ok(controller.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Remover cliente", description = "Remove um cliente existente pelo ID.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        controller.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar por ID", description = "Busca um cliente pelo seu ID único.")
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(controller.getById(id));
    }

    @GetMapping("/document/{document}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar por CPF/CNPJ", description = "Busca um cliente pelo seu CPF ou CNPJ.")
    public ResponseEntity<CustomerResponse> getByDocument(@PathVariable String document) {
        return ResponseEntity.ok(controller.getByDocument(document));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados.")
    public ResponseEntity<List<CustomerResponse>> listAll() {
        // TODO: Will be necessary to implement pagination in the future
        return ResponseEntity.ok(controller.listAll());
    }

}
