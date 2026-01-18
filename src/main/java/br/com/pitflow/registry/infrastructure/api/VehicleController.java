package br.com.pitflow.registry.infrastructure.api;

import br.com.pitflow.registry.aplication.dto.AddVehicleDto;
import br.com.pitflow.registry.aplication.dto.UpdateVehicleDto;
import br.com.pitflow.registry.aplication.usecases.AddVehicle;
import br.com.pitflow.registry.aplication.usecases.DeleteVehicle;
import br.com.pitflow.registry.aplication.usecases.FindVehicleById;
import br.com.pitflow.registry.aplication.usecases.FindVehicleByPlate;
import br.com.pitflow.registry.aplication.usecases.FindVehiclesByCustomerId;
import br.com.pitflow.registry.aplication.usecases.ListVehicles;
import br.com.pitflow.registry.aplication.usecases.UpdateVehicle;
import br.com.pitflow.registry.domain.Vehicle;
import br.com.pitflow.registry.infrastructure.api.dto.VehicleResponse;
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
@RequestMapping("/registry/vehicles")
@Tag(name = "Registry - Vehicles", description = "Gerenciamento do cadastro de veículos dos clientes")
public class VehicleController {

    private final AddVehicle addVehicle;
    private final UpdateVehicle updateVehicle;
    private final DeleteVehicle deleteVehicle;
    private final FindVehicleById findVehicleById;
    private final FindVehicleByPlate findVehicleByPlate;
    private final FindVehiclesByCustomerId findVehiclesByCustomerId;
    private final ListVehicles listVehicles;

    public VehicleController(
            AddVehicle addVehicle,
            UpdateVehicle updateVehicle,
            DeleteVehicle deleteVehicle,
            FindVehicleById findVehicleById,
            FindVehicleByPlate findVehicleByPlate,
            FindVehiclesByCustomerId findVehiclesByCustomerId,
            ListVehicles listVehicles
    ) {
        this.addVehicle = addVehicle;
        this.updateVehicle = updateVehicle;
        this.deleteVehicle = deleteVehicle;
        this.findVehicleById = findVehicleById;
        this.findVehicleByPlate = findVehicleByPlate;
        this.findVehiclesByCustomerId = findVehiclesByCustomerId;
        this.listVehicles = listVehicles;
    }

    @PostMapping
    @Operation(summary = "Adicionar veículo", description = "Vincula um novo veículo a um cliente existente.")
    public ResponseEntity<VehicleResponse> create(@RequestBody AddVehicleDto dto) {
        var vehicle = addVehicle.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(vehicle));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo", description = "Altera os dados técnicos ou a placa de um veículo cadastrado.")
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @RequestBody UpdateVehicleDto dto) {
        updateVehicle.execute(id, dto);
        var updated = findVehicleById.execute(id);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover veículo", description = "Exclui permanentemente o veículo da base de dados.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteVehicle.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Recupera os detalhes de um veículo através do seu identificador único.")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        var vehicle = findVehicleById.execute(id);
        return ResponseEntity.ok(toResponse(vehicle));
    }

    @GetMapping("/plate/{plate}")
    @Operation(summary = "Buscar por Placa", description = "Localiza um veículo no sistema utilizando a placa (License Plate).")
    public ResponseEntity<VehicleResponse> getByPlate(@PathVariable String plate) {
        var vehicle = findVehicleByPlate.execute(plate);
        return ResponseEntity.ok(toResponse(vehicle));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Listar por Cliente", description = "Retorna todos os veículos associados a um determinado ID de cliente.")
    public ResponseEntity<List<VehicleResponse>> getByCustomerId(@PathVariable UUID customerId) {
        var vehicles = findVehiclesByCustomerId.execute(customerId);
        var response = vehicles.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos", description = "Retorna uma lista global de todos os veículos cadastrados na oficina.")
    public ResponseEntity<List<VehicleResponse>> listAll() {
        var vehicles = listVehicles.execute();
        var response = vehicles.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Mapper privado para converter a Entidade de Domínio para o DTO de Resposta da API.
     */
    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getCustomerId(),
                vehicle.getLicensePlate().value(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear()
        );
    }
}
