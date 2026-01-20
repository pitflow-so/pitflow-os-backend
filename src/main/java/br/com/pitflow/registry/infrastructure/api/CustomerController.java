package br.com.pitflow.registry.infrastructure.api;

import br.com.pitflow.registry.application.dto.CreateCustomerDto;
import br.com.pitflow.registry.application.dto.UpdateCustomerDto;
import br.com.pitflow.registry.application.usecases.CreateCustomer;
import br.com.pitflow.registry.application.usecases.DeleteCustomer;
import br.com.pitflow.registry.application.usecases.FindCustomerByDocument;
import br.com.pitflow.registry.application.usecases.FindCustomerById;
import br.com.pitflow.registry.application.usecases.ListCustomers;
import br.com.pitflow.registry.application.usecases.UpdateCustomer;
import br.com.pitflow.registry.domain.Customer;
import br.com.pitflow.registry.infrastructure.api.dto.CustomerResponse;
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
public class CustomerController {

    private final CreateCustomer createCustomer;
    private final UpdateCustomer updateCustomer;
    private final DeleteCustomer deleteCustomer;
    private final FindCustomerById findCustomerById;
    private final FindCustomerByDocument findCustomerByDocument;
    private final ListCustomers listCustomers;

    public CustomerController(CreateCustomer createCustomer, UpdateCustomer updateCustomer, DeleteCustomer deleteCustomer, FindCustomerById findCustomerById, FindCustomerByDocument findCustomerByDocument, ListCustomers listCustomers) {
        this.createCustomer = createCustomer;
        this.updateCustomer = updateCustomer;
        this.deleteCustomer = deleteCustomer;
        this.findCustomerById = findCustomerById;
        this.findCustomerByDocument = findCustomerByDocument;
        this.listCustomers = listCustomers;
    }

    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cria um novo cliente com os dados fornecidos.")
    public ResponseEntity<CustomerResponse> create(@RequestBody CreateCustomerDto dto) {
        var customer = createCustomer.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(customer));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, @RequestBody UpdateCustomerDto dto) {
        updateCustomer.execute(id, dto);
        var updated = findCustomerById.execute(id);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Remover cliente", description = "Remove um cliente existente pelo ID.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCustomer.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar por ID", description = "Busca um cliente pelo seu ID único.")
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        var customer = findCustomerById.execute(id);
        return ResponseEntity.ok(toResponse(customer));
    }

    @GetMapping("/document/{document}")
    @Operation(summary = "Buscar por CPF/CNPJ", description = "Busca um cliente pelo seu CPF ou CNPJ.")
    public ResponseEntity<CustomerResponse> getByDocument(@PathVariable String document) {
        var customer = findCustomerByDocument.execute(document);
        return ResponseEntity.ok(toResponse(customer));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados.")
    public ResponseEntity<List<CustomerResponse>> listAll() {
        var customers = listCustomers.execute();
        // TODO: Will be necessary to implement pagination in the future
        var response = customers.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getDocument().value(), customer.getPhone());
    }
}
