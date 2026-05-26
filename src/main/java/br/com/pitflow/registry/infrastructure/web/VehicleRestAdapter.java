package br.com.pitflow.registry.infrastructure.web;

import br.com.pitflow.registry.infrastructure.web.dto.AddVehicleRequest;
import br.com.pitflow.registry.controller.VehicleController;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateVehicleRequest;
import br.com.pitflow.registry.presenter.dto.VehicleResponse;
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
@RequestMapping("/registry/vehicles")
@Tag(name = "Registry - Vehicles", description = "Gerenciamento do cadastro de veículos dos clientes")
//@SecurityRequirement(name = "bearerAuth") // Protege a classe toda por padrão
public class VehicleRestAdapter {

    private final VehicleController vehicleController;

    public VehicleRestAdapter(VehicleController vehicleController) {
        this.vehicleController = vehicleController;
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Adicionar veículo", description = "Vincula um novo veículo a um cliente existente.")
    public ResponseEntity<VehicleResponse> create(@RequestBody AddVehicleRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleController.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar veículo", description = "Altera os dados técnicos ou a placa de um veículo cadastrado.")
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @RequestBody UpdateVehicleRequest dto) {
        return ResponseEntity.ok(vehicleController.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Remover veículo", description = "Exclui permanentemente o veículo da base de dados.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleController.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar por ID", description = "Recupera os detalhes de um veículo através do seu identificador único.")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleController.findById(id));
    }

    @GetMapping("/plate/{plate}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar por Placa", description = "Localiza um veículo no sistema utilizando a placa (License Plate).")
    public ResponseEntity<VehicleResponse> getByPlate(@PathVariable String plate) {
        return ResponseEntity.ok(vehicleController.findByPlate(plate));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar por Cliente", description = "Retorna todos os veículos associados a um determinado ID de cliente.")
    public ResponseEntity<List<VehicleResponse>> getByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(vehicleController.findByCustomerId(customerId));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar todos", description = "Retorna uma lista global de todos os veículos cadastrados na oficina.")
    public ResponseEntity<List<VehicleResponse>> listAll() {
        return ResponseEntity.ok(vehicleController.findAll());
    }

}
