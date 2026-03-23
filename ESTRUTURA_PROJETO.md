# Estrutura do Projeto - Pitflow OS Backend

## Visão Geral

Projeto backend em Java 21 com Spring Boot, organizado com uma abordagem inspirada em DDD e dividido em quatro contextos principais:

- `common`: segurança, configurações e value objects compartilhados.
- `registry`: cadastro de clientes, veículos e mecânicos.
- `operation`: ciclo de vida das ordens de serviço.
- `inventory`: catálogo de peças e serviços.

## Estrutura Resumida

```text
pitflow-os-backend/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/br/com/pitflow/
│   │   │   ├── PitflowOsBackendApplication.java
│   │   │   ├── common/
│   │   │   ├── registry/
│   │   │   ├── operation/
│   │   │   └── inventory/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/changelog/migrations/
│   └── test/
│       └── java/br/com/pitflow/
│           ├── common/
│           ├── inventory/
│           ├── operation/
│           └── registry/
└── target/
```

## Fluxo Arquitetural Principal

```text
Controller REST
  -> Use Case (application)
  -> Entidade de domínio (domain)
  -> Repositório por interface (domain/repository)
  -> Adapter JPA (infrastructure/persistence/adapter)
  -> Spring Data Repository / Entity / Mapper
```

## Classes Principais

### 1. Classe de inicialização

Arquivo: `src/main/java/br/com/pitflow/PitflowOsBackendApplication.java`

```java
package br.com.pitflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PitflowOsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PitflowOsBackendApplication.class, args);
    }

}
```

### 2. Configuração de segurança

Arquivo: `src/main/java/br/com/pitflow/common/infrastructure/configuration/SecurityConfig.java`

```java
package br.com.pitflow.common.infrastructure.configuration;

import br.com.pitflow.common.infrastructure.security.SecurityFilter;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.core.userdetails.User.builder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/registry/auth/**").permitAll()
                        .requestMatchers(POST, "/registry/mechanics").permitAll()
                        .requestMatchers(POST, "/registry/vehicles").permitAll()
                        .requestMatchers(GET, "/registry/vehicles/plate/{plate}").permitAll()
                        .requestMatchers(POST, "/registry/customers").permitAll()
                        .requestMatchers(GET, "/registry/customers/document/{document}").permitAll()
                        .requestMatchers(POST, "/operation/service-orders").permitAll()
                        .requestMatchers(PATCH, "/operation/service-orders/{id}/approve").permitAll()
                        .requestMatchers(PATCH, "/operation/service-orders/{id}/cancel").permitAll()
                        .requestMatchers(GET, "/operation/service-orders/{id}").permitAll()
                        .requestMatchers(GET, "/operation/service-orders/{id}/duration").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(MechanicGateway repository) {
        return username -> {
            var mechanic = repository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Mechanic not found: " + username));

            return builder()
                    .username(mechanic.getUsername())
                    .password(mechanic.getPassword())
                    .authorities(mechanic.getRole())
                    .build();
        };
    }

}
```

### 3. Filtro JWT

Arquivo: `src/main/java/br/com/pitflow/common/infrastructure/security/SecurityFilter.java`

```java
package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.registry.core.gateway.MechanicGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MechanicGateway mechanicGateway;

    public SecurityFilter(JwtService jwtService, MechanicGateway mechanicGateway) {
        this.jwtService = jwtService;
        this.mechanicGateway = mechanicGateway;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var token = this.recoverToken(request);

        if (token != null) {
            var username = jwtService.validateToken(token);

            if (username != null) {
                var mechanic = mechanicGateway.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Mechanic not found"));

                var userDetails = User.builder()
                        .username(mechanic.getUsername())
                        .password(mechanic.getPassword())
                        .authorities(mechanic.getRole())
                        .build();

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
```

### 4. Entidade principal de cadastro de clientes

Arquivo: `src/main/java/br/com/pitflow/registry/domain/Customer.java`

```java
package br.com.pitflow.registry.domain;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;
    private CpfCnpj document;
    private String phone;
    private List<Vehicle> vehicles;

    public Customer(String name, CpfCnpj document, String phone) {
        validateName(name);
        this.id = UUID.randomUUID();
        this.name = name;
        this.document = document;
        this.phone = phone;
        this.vehicles = new ArrayList<>();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CpfCnpj getDocument() {
        return document;
    }

    public String getPhone() {
        return phone;
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setDocument(CpfCnpj newDocument) {
        this.document = newDocument;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
```

### 5. Caso de uso principal de cadastro

Arquivo: `src/main/java/br/com/pitflow/registry/application/CreateCustomerImp.java`

```java
package br.com.pitflow.registry.application;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.core.gateway.CustomerGateway;import br.com.pitflow.registry.infrastructure.web.dto.CreateCustomerRequest;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.entity.Customer;

public class CreateCustomerImp implements br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer {
    private final CustomerGateway repository;

    public CreateCustomerImp(CustomerGateway repository) {
        this.repository = repository;
    }

    @Override
    public Customer execute(CreateCustomerRequest dto) {
        var document = new CpfCnpj(dto.document());

        repository.findByDocument(document).ifPresent(customer -> {
            var message = String.format("Customer with document %s already exists.", document.value());
            throw new IllegalStateException(message);
        });

        var customer = new Customer(dto.name(), document, dto.phone());
        repository.save(customer);
        return customer;
    }
}
```

### 6. Controller principal do contexto Registry

Arquivo: `src/main/java/br/com/pitflow/registry/infrastructure/api/CustomerController.java`

```java
package br.com.pitflow.registry.infrastructure.web;

import br.com.pitflow.registry.infrastructure.web.dto.CreateCustomerRequest;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateCustomerRequest;
import br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.DeleteCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerByDocument;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerById;
import br.com.pitflow.registry.core.usecase.customer.inputPort.ListCustomers;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;
import br.com.pitflow.registry.core.entity.Customer;
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
public class CustomerController {

    private final br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer createCustomer;
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
    public ResponseEntity<CustomerResponse> create(@RequestBody CreateCustomerRequest dto) {
        var customer = createCustomer.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(customer));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    public ResponseEntity<CustomerResponse> update(@PathVariable UUID id, @RequestBody UpdateCustomerRequest dto) {
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
        var response = customers.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getDocument().value(), customer.getPhone());
    }
}
```

### 7. Adapter de persistência do contexto Registry

Arquivo: `src/main/java/br/com/pitflow/registry/infrastructure/persistence/adapter/JpaCustomerRepositoryAdapter.java`

```java
package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.common.valueobject.CpfCnpj;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.infrastructure.persistence.mapper.CustomerMapper;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringCustomerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaCustomerRepositoryAdapter implements CustomerGateway {
    private final SpringCustomerRepository repository;

    public JpaCustomerRepositoryAdapter(SpringCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Customer customer) {
        var entity = CustomerMapper.toEntity(customer);
        repository.save(entity);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return repository.findById(id)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByDocument(CpfCnpj document) {
        return repository.findByDocument(document.value())
                .map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll().stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
```

### 8. Entidade central de ordem de serviço

Arquivo: `src/main/java/br/com/pitflow/operation/domain/ServiceOrder.java`

```java
package br.com.pitflow.operation.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class ServiceOrder {
    private UUID id;
    private final UUID customerId;
    private final UUID vehicleId;
    private Status status;
    private final List<Item> items;
    private final String description;

    private LocalDateTime createdAt;
    private LocalDateTime executionStartedAt;
    private LocalDateTime diagnosisStartedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime deliveredAt;

    private String cancellationDescription;

    public ServiceOrder(UUID customerId, UUID vehicleId, String description) {
        descriptionValidate(description);
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.status = Status.RECEIVED;
        this.items = new ArrayList<>();
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    private void descriptionValidate(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Service order description cannot be empty.");
        }
    }

    public enum Status {
        RECEIVED, IN_DIAGNOSIS, AWAITING_APPROVAL, IN_EXECUTION, FINISHED, DELIVERED, CANCELLED
    }

    public enum ItemType {PART, SERVICE}

    public static record Item(UUID catalogId, String description, BigDecimal unitPrice, int quantity, ItemType type) {
        public BigDecimal getTotalPrice() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void addPart(UUID partId, String name, BigDecimal currentPrice, int quantity) {
        validateModificationState();
        this.items.add(new Item(partId, name, currentPrice, quantity, ItemType.PART));
    }

    public void addService(UUID serviceId, String name, BigDecimal currentPrice) {
        validateModificationState();
        this.items.add(new Item(serviceId, name, currentPrice, 1, ItemType.SERVICE));
    }

    private void validateModificationState() {
        if (this.status != Status.RECEIVED && this.status != Status.IN_DIAGNOSIS) {
            throw new IllegalStateException("Items can only be added during RECEIVED or IN_DIAGNOSIS stages.");
        }
    }

    public void approve() {
        if (this.status != Status.AWAITING_APPROVAL) {
            throw new IllegalStateException("Order must be AWAITING_APPROVAL to be approved.");
        }
        this.status = Status.IN_EXECUTION;
        this.executionStartedAt = LocalDateTime.now();
    }

    public void cancel(String cancellationMessage) {
        if (EnumSet.of(Status.FINISHED, Status.DELIVERED).contains(this.status)) {
            throw new IllegalStateException("Cannot cancel an order that is already finished or delivered.");
        }
        this.cancellationDescription = cancellationMessage;
        this.status = Status.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void startDiagnosis() {
        if (this.status != Status.RECEIVED) {
            throw new IllegalStateException("Order must be RECEIVED to start diagnosis.");
        }
        this.status = Status.IN_DIAGNOSIS;
        this.diagnosisStartedAt = LocalDateTime.now();
    }

    public void completeDiagnosis() {
        if (this.status != Status.IN_DIAGNOSIS) {
            throw new IllegalStateException("Order must be IN_DIAGNOSIS to complete diagnosis.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot complete diagnosis without adding at least one item.");
        }
        this.status = Status.AWAITING_APPROVAL;
    }

    public void finish() {
        if (this.status != Status.IN_EXECUTION) {
            throw new IllegalStateException("Order must be IN_EXECUTION to be finished.");
        }
        this.status = Status.FINISHED;
        this.finishedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (this.status != Status.FINISHED) {
            throw new IllegalStateException("Order must be FINISHED to be delivered.");
        }
        this.status = Status.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() { return id; }
    public Status getStatus() { return status; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExecutionStartedAt() { return executionStartedAt; }
    public LocalDateTime getDiagnosisStartedAt() { return diagnosisStartedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public String getCancellationDescription() { return cancellationDescription; }
    public UUID getCustomerId() { return customerId; }
    public UUID getVehicleId() { return vehicleId; }

    public void setId(UUID id){
        this.id = id;
    }
    public void reconstituteStatus(Status status) {
        this.status = status;
    }
    public void reconstituteItems(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
    }
    public void reconstituteCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void reconstituteDiagnosisStartedAt(LocalDateTime diagnosisStartedAt) {
        this.diagnosisStartedAt = diagnosisStartedAt;
    }
    public void reconstituteExecutionStartedAt(LocalDateTime executionStartedAt) {
        this.executionStartedAt = executionStartedAt;
    }
    public void reconstituteFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public void reconstituteDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void reconstituteCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void reconstituteCancellationDescription(String description) {
        this.cancellationDescription = description;
    }
}
```

### 9. Caso de uso principal de abertura de OS

Arquivo: `src/main/java/br/com/pitflow/operation/application/CreateServiceOrderImp.java`

```java
package br.com.pitflow.operation.application;

import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
import br.com.pitflow.operation.application.usecase.CreateServiceOrder;
import br.com.pitflow.operation.domain.ServiceOrder;
import br.com.pitflow.operation.domain.repository.ServiceOrderRepository;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.gateway.VehicleRepository;

public class CreateServiceOrderImp implements CreateServiceOrder {

    private final ServiceOrderRepository serviceOrderRepository;
    private final CustomerGateway customerGateway;
    private final VehicleGateway vehicleGateway;

    public CreateServiceOrderImp(
            ServiceOrderRepository serviceOrderRepository,
            CustomerGateway customerGateway,
            VehicleGateway vehicleGateway) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.customerGateway = customerGateway;
        this.vehicleGateway = vehicleGateway;
    }

    @Override
    public ServiceOrder execute(CreateServiceOrderDto dto) {
        customerGateway.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + dto.customerId()));

        var vehicle = vehicleGateway.findById(dto.vehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + dto.vehicleId()));

        if (!vehicle.getCustomerId().equals(dto.customerId())) {
            throw new IllegalStateException("The informed vehicle does not belong to the informed customer.");
        }

        var serviceOrder = new ServiceOrder(dto.customerId(), dto.vehicleId(), dto.description());

        serviceOrderRepository.save(serviceOrder);
        return serviceOrder;
    }
}
```

### 10. Controller principal do contexto Operation

Arquivo: `src/main/java/br/com/pitflow/operation/infrastructure/api/ServiceOrderController.java`

```java
package br.com.pitflow.operation.infrastructure.api;

import br.com.pitflow.operation.application.dto.AddOrderItemDto;
import br.com.pitflow.operation.application.dto.CancelOrderDto;
import br.com.pitflow.operation.application.dto.CreateServiceOrderDto;
import br.com.pitflow.operation.application.usecase.AddOrderItem;
import br.com.pitflow.operation.application.usecase.ApproveOrder;
import br.com.pitflow.operation.application.usecase.CancelOrder;
import br.com.pitflow.operation.application.usecase.CompleteDiagnosis;
import br.com.pitflow.operation.application.usecase.CreateServiceOrder;
import br.com.pitflow.operation.application.usecase.DeliverOrder;
import br.com.pitflow.operation.application.usecase.FindAllServiceOrders;
import br.com.pitflow.operation.application.usecase.FinishOrder;
import br.com.pitflow.operation.application.usecase.GetAverageExecutionTime;
import br.com.pitflow.operation.application.usecase.GetServiceOrderById;
import br.com.pitflow.operation.application.usecase.GetServiceOrderDuration;
import br.com.pitflow.operation.application.usecase.ListInExecutionOrders;
import br.com.pitflow.operation.application.usecase.StartDiagnosis;
import br.com.pitflow.operation.infrastructure.api.dto.ExecutionTimeMetricsResponse;
import br.com.pitflow.operation.infrastructure.api.dto.OrderDurationResponse;
import br.com.pitflow.operation.infrastructure.api.dto.ServiceOrderResponse;
import br.com.pitflow.operation.infrastructure.api.mapper.ServiceOrderApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operation/service-orders")
@Tag(name = "Operation - Service Orders", description = "Endpoints para gestão do ciclo de vida das Ordens de Serviço")
public class ServiceOrderController {

    private final AddOrderItem addOrderItem;
    private final ApproveOrder approveOrder;
    private final CancelOrder cancelOrder;
    private final CompleteDiagnosis completeDiagnosis;
    private final CreateServiceOrder createServiceOrder;
    private final DeliverOrder deliverOrder;
    private final FindAllServiceOrders findAllServiceOrders;
    private final FinishOrder finishOrder;
    private final GetServiceOrderById getServiceOrderById;
    private final StartDiagnosis startDiagnosis;
    private final ListInExecutionOrders listInExecutionOrders;
    private final GetAverageExecutionTime getAverageExecutionTime;
    private final GetServiceOrderDuration getServiceOrderDuration;

    public ServiceOrderController(CreateServiceOrder createServiceOrder, AddOrderItem addOrderItem, StartDiagnosis startDiagnosis, CompleteDiagnosis completeDiagnosis, ApproveOrder approveOrder, FinishOrder finishOrder, DeliverOrder deliverOrder, CancelOrder cancelOrder, GetServiceOrderById getServiceOrderById, FindAllServiceOrders findAllServiceOrders, ListInExecutionOrders listInExecutionOrders, GetAverageExecutionTime getAverageExecutionTime, GetServiceOrderDuration getServiceOrderDuration) {
        this.createServiceOrder = createServiceOrder;
        this.addOrderItem = addOrderItem;
        this.startDiagnosis = startDiagnosis;
        this.completeDiagnosis = completeDiagnosis;
        this.approveOrder = approveOrder;
        this.finishOrder = finishOrder;
        this.deliverOrder = deliverOrder;
        this.cancelOrder = cancelOrder;
        this.getServiceOrderById = getServiceOrderById;
        this.findAllServiceOrders = findAllServiceOrders;
        this.listInExecutionOrders = listInExecutionOrders;
        this.getAverageExecutionTime = getAverageExecutionTime;
        this.getServiceOrderDuration = getServiceOrderDuration;
    }

    @PostMapping
    @Operation(summary = "Abre uma nova Ordem de Serviço", description = "Status inicial: RECEIVED")
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody CreateServiceOrderDto dto) {
        var domain = createServiceOrder.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ServiceOrderApiMapper.toResponse(domain));
    }

    @PostMapping("/{id}/items")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Adiciona peça ou serviço à OS", description = "Permitido apenas nos status RECEIVED ou IN_DIAGNOSIS")
    public ResponseEntity<Void> addItem(@PathVariable UUID id, @RequestBody AddOrderItemDto dto) {
        var updateDto = new AddOrderItemDto(id, dto.catalogId(), dto.quantity(), dto.type());
        addOrderItem.execute(updateDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/start-diagnosis")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Inicia a análise técnica, para definir serviços e peças", description = "Muda status para IN_DIAGNOSIS")
    public ResponseEntity<Void> startDiagnosis(@PathVariable UUID id) {
        startDiagnosis.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete-diagnosis")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Finaliza a análise técnica e notifica o cliente", description = "Muda status para AWAITING_APPROVAL")
    public ResponseEntity<Void> completeDiagnosis(@PathVariable UUID id) {
        completeDiagnosis.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Aprova o orçamento", description = "Muda status para IN_EXECUTION")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        approveOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finish")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Finaliza a execução dos serviços (Mão de Obra)", description = "Muda status para FINISHED")
    public ResponseEntity<Void> finish(@PathVariable UUID id) {
        finishOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deliver")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Registra a entrega do veículo", description = "Muda status para DELIVERED")
    public ResponseEntity<Void> deliver(@PathVariable UUID id) {
        deliverOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancela a Ordem de Serviço", description = "Exige motivo. Não permitido para OS finalizadas.")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, @RequestBody String reason) {
        cancelOrder.execute(new CancelOrderDto(id, reason));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca detalhes de uma OS específica")
    public ResponseEntity<ServiceOrderResponse> getById(@PathVariable UUID id) {
        var domain = getServiceOrderById.execute(id);
        return ResponseEntity.ok(ServiceOrderApiMapper.toResponse(domain));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Lista todas as Ordens de Serviço da oficina, em ordem do mais antigo para o mais novo", description = "Mecânico pode visualizar todas as ordens")
    public ResponseEntity<List<ServiceOrderResponse>> getAll() {
        var list = findAllServiceOrders.execute().stream().map(ServiceOrderApiMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/in-execution")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Lista ordens prontas para execução", description = "Retorna a fila de trabalho do mecânico (Status: IN_EXECUTION) ordenada pela data de criação mais antiga.")
    public ResponseEntity<List<ServiceOrderResponse>> listInExecution() {
        var list = listInExecutionOrders.execute().stream().map(ServiceOrderApiMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/metrics/average-execution-time")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Obter tempo médio de execução", description = "Calcula a média de tempo que os serviços levam para serem concluídos.")
    public ResponseEntity<ExecutionTimeMetricsResponse> getAverageTime() {
        return ResponseEntity.ok(getAverageExecutionTime.execute());
    }

    @GetMapping("/{id}/duration")
    @Operation(summary = "Obter duração da OS", description = "Retorna o tempo decorrido desde o início da execução.")
    public ResponseEntity<OrderDurationResponse> getDuration(@PathVariable UUID id) {
        return ResponseEntity.ok(getServiceOrderDuration.execute(id));
    }
}
```

### 11. Entidade central do contexto Inventory

Arquivo: `src/main/java/br/com/pitflow/inventory/domain/Part.java`

```java
package br.com.pitflow.inventory.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Part {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;

    public Part(String sku, String name, String description, BigDecimal price, int initialStock) {
        validateName(name);
        validatePrice(price);
        validateStock(initialStock);
        validateSku(sku);

        this.id = UUID.randomUUID();
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = initialStock;
    }

    private void validateSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Part SKU cannot be empty.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Part name cannot be empty.");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Part price must be greater than zero.");
        }
    }

    private void validateStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
    }

    public void addStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to add must be positive.");
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity to remove must be positive.");
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock for part: " + name);
        }
        this.stockQuantity -= quantity;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public void setId(UUID id) { this.id = id; }
    public void setSku(String sku) {
        validateSku(sku);
        this.sku = sku;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        validatePrice(price);
        this.price = price;
    }

    public void setStockQuantity(int i) {
        validateStock(i);
        this.stockQuantity = i;
    }
}
```

### 12. Controller principal do contexto Inventory

Arquivo: `src/main/java/br/com/pitflow/inventory/infrastructure/api/PartController.java`

```java
package br.com.pitflow.inventory.infrastructure.web;

import br.com.pitflow.inventory.infrastructure.web.dto.UpdatePartRequest;
import br.com.pitflow.inventory.infrastructure.web.dto.CreatePartRequest;
import br.com.pitflow.inventory.core.usecase.part.inputPort.CreatePart;
import br.com.pitflow.inventory.core.usecase.part.inputPort.DeletePart;
import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartById;
import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartBySku;
import br.com.pitflow.inventory.core.usecase.part.inputPort.ListParts;
import br.com.pitflow.inventory.core.usecase.part.inputPort.UpdatePart;
import br.com.pitflow.inventory.core.entity.Part;
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
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Cadastrar nova peça", description = "Adiciona uma peça ao inventário com SKU único.")
    public ResponseEntity<PartResponse> create(@RequestBody CreatePartRequest dto) {
        var part = createPart.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(part));
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar peça por ID")
    public ResponseEntity<PartResponse> getById(@PathVariable UUID id) {
        var part = findPartById.execute(id);
        return ResponseEntity.ok(toResponse(part));
    }

    @GetMapping("/sku/{sku}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Buscar peça po SKU")
    public ResponseEntity<PartResponse> getBySku(@PathVariable String sku) {
        var part = findPartBySku.execute(sku);
        return ResponseEntity.ok(toResponse(part));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Listar todas as peças")
    public ResponseEntity<List<PartResponse>> listAll() {
        var parts = listParts.execute();
        var response = parts.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Atualizar peça", description = "Atualiza os dados de uma peça existente.")
    public ResponseEntity<PartResponse> update(@PathVariable UUID id, @RequestBody UpdatePartRequest dto) {
        updatePart.execute(id, dto);
        var updatedPart = findPartById.execute(id);
        return ResponseEntity.ok(toResponse(updatedPart));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"), summary = "Excluir peça", description = "Remove permanentemente uma peça do inventário.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deletePart.execute(id);
        return ResponseEntity.noContent().build();
    }

    private PartResponse toResponse(Part part) {
        return new PartResponse(part.getId(), part.getSku(), part.getName(),
                part.getDescription(), part.getPrice(), part.getStockQuantity());
    }
}
```

### 13. Adapter de persistência do Inventory

Arquivo: `src/main/java/br/com/pitflow/inventory/infrastructure/persistence/adapter/JpaPartRepositoryAdapter.java`

```java
package br.com.pitflow.inventory.infrastructure.persistence.adapter;

import br.com.pitflow.inventory.core.entity.Part;
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.PartRepository;
import br.com.pitflow.inventory.infrastructure.persistence.mapper.PartMapper;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringPartRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JpaPartRepositoryAdapter implements PartGateway {
    private final SpringPartRepository springPartRepository;

    public JpaPartRepositoryAdapter(SpringPartRepository springPartRepository) {
        this.springPartRepository = springPartRepository;
    }

    @Override
    public void save(Part part) {
        var entity = PartMapper.toEntity(part);
        springPartRepository.save(entity);
    }

    @Override
    public Optional<Part> findById(UUID id) {
        return springPartRepository.findById(id)
                .map(PartMapper::toDomain);
    }

    @Override
    public Optional<Part> findBySku(String sku) {
        return springPartRepository.findBySku(sku)
                .map(PartMapper::toDomain);
    }

    @Override
    public List<Part> findAll() {
        return springPartRepository.findAll().stream()
                .map(PartMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springPartRepository.deleteById(id);
    }
}
```

## Configuração principal da aplicação

Arquivo: `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: pitflow-os-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/pitflow_os
    username: admin
    password: admin
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    open-in-view: false
    properties:
      hibernate:
        format_sql: true

  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    enabled: true

logging:
  level:
    org:
      liquibase: INFO

api:
  security:
    token:
      secret: ${JWT_SECRET:digimon-digimon-digitais-digimons-sao-campeoes-123456789}
      expiration-hours: 3
```

## Resumo

Esse projeto segue um desenho consistente em camadas:

- `domain` concentra as regras de negócio.
- `application` orquestra os casos de uso.
- `infrastructure/api` expõe os endpoints REST.
- `infrastructure/persistence` integra com JPA e PostgreSQL.
- `common` centraliza segurança JWT, configurações e objetos de valor.
