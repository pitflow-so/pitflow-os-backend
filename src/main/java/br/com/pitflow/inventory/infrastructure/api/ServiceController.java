package br.com.pitflow.inventory.infrastructure.api;

import br.com.pitflow.inventory.application.dto.CreateServiceDto;
import br.com.pitflow.inventory.application.dto.UpdateServiceDto;
import br.com.pitflow.inventory.application.usecase.CreateService;
import br.com.pitflow.inventory.application.usecase.DeleteService;
import br.com.pitflow.inventory.application.usecase.FindServiceById;
import br.com.pitflow.inventory.application.usecase.ListServices;
import br.com.pitflow.inventory.application.usecase.UpdateService;
import br.com.pitflow.inventory.domain.Service;
import br.com.pitflow.inventory.infrastructure.api.dto.ServiceResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/inventory/services")
@Tag(name = "Inventory - Services", description = "Endpoints para gestão do catálogo de serviços (Mão de Obra)")
public class ServiceController {

    private final CreateService createService;
    private final FindServiceById findServiceById;
    private final ListServices listServices;
    private final UpdateService updateService;
    private final DeleteService deleteService;

    public ServiceController(
            CreateService createService,
            FindServiceById findServiceById,
            ListServices listServices,
            UpdateService updateService,
            DeleteService deleteService
    ) {
        this.createService = createService;
        this.findServiceById = findServiceById;
        this.listServices = listServices;
        this.updateService = updateService;
        this.deleteService = deleteService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo serviço", description = "Registra um serviço de mão de obra no catálogo de serviços da oficina.")
    public ResponseEntity<ServiceResponse> create(@RequestBody CreateServiceDto dto) {
        var service = createService.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(service));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID", description = "Recupera os detalhes de um serviço específico usando seu identificador único.")
    public ResponseEntity<ServiceResponse> getById(@PathVariable UUID id) {
        var service = findServiceById.execute(id);
        return ResponseEntity.ok(toResponse(service));
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista de todos os serviços cadastrados no catálogo.")
    public ResponseEntity<java.util.List<ServiceResponse>> listAll() {
        var services = listServices.execute();
        var response = services.stream().map(this::toResponse).toList();
        //TODO: Implement pagination
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço", description = "Atualiza o nome, descrição ou preço de um serviço de mão de obra.")
    public ResponseEntity<ServiceResponse> update(@PathVariable UUID id, @RequestBody UpdateServiceDto dto) {
        updateService.execute(id, dto);
        var updatedService = findServiceById.execute(id);
        return ResponseEntity.ok(toResponse(updatedService));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir serviço", description = "Remove um serviço do catálogo de mão de obra.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteService.execute(id);
        return ResponseEntity.noContent().build();
    }

    private ServiceResponse toResponse(Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice()
        );
    }
}
