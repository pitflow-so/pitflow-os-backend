package br.com.pitflow.registry.infrastructure.config;

import br.com.pitflow.common.core.gateway.PasswordVerifierGateway;
import br.com.pitflow.common.core.gateway.TokenGateway;
import br.com.pitflow.registry.controller.AuthController;
import br.com.pitflow.registry.controller.CustomerController;
import br.com.pitflow.registry.controller.MechanicController;
import br.com.pitflow.registry.controller.VehicleController;
import br.com.pitflow.registry.core.gateway.CustomerGateway;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import br.com.pitflow.registry.core.gateway.PasswordEncoderGateway;
import br.com.pitflow.registry.core.gateway.VehicleGateway;
import br.com.pitflow.registry.core.usecase.customer.CreateCustomerImp;
import br.com.pitflow.registry.core.usecase.customer.DeleteCustomerImp;
import br.com.pitflow.registry.core.usecase.customer.FindCustomerByDocumentImp;
import br.com.pitflow.registry.core.usecase.customer.FindCustomerByIdImp;
import br.com.pitflow.registry.core.usecase.customer.ListCustomersImp;
import br.com.pitflow.registry.core.usecase.customer.UpdateCustomerImp;
import br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.DeleteCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerByDocument;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerById;
import br.com.pitflow.registry.core.usecase.customer.inputPort.ListCustomers;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;
import br.com.pitflow.registry.core.usecase.mechanic.AuthenticateMechanicImp;
import br.com.pitflow.registry.core.usecase.mechanic.CreateMechanicImp;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.AuthenticateMechanic;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.CreateMechanic;
import br.com.pitflow.registry.core.usecase.vehicle.AddVehicleImp;
import br.com.pitflow.registry.core.usecase.vehicle.DeleteVehicleImp;
import br.com.pitflow.registry.core.usecase.vehicle.FindVehicleByIdImp;
import br.com.pitflow.registry.core.usecase.vehicle.FindVehicleByPlateImp;
import br.com.pitflow.registry.core.usecase.vehicle.FindVehiclesByCustomerIdImp;
import br.com.pitflow.registry.core.usecase.vehicle.ListVehiclesImp;
import br.com.pitflow.registry.core.usecase.vehicle.UpdateVehicleImp;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.AddVehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.DeleteVehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleById;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleByPlate;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehiclesByCustomerId;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.ListVehicles;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.UpdateVehicle;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaCustomerGatewayAdapter;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaMechanicGatewayAdapter;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaVehicleGatewayAdapter;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringCustomerRepository;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringMechanicRepository;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringVehicleRepository;
import br.com.pitflow.registry.infrastructure.security.BcryptPasswordEncoderAdapter;
import br.com.pitflow.registry.infrastructure.security.BcryptPasswordVerifierAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanRegistryConfig {

    @Bean
    public CustomerGateway customerGateway(SpringCustomerRepository repository) {
        return new JpaCustomerGatewayAdapter(repository);
    }

    @Bean
    public CreateCustomer createCustomer(CustomerGateway repository) {
        return new CreateCustomerImp(repository);
    }

    @Bean
    public UpdateCustomer updateCustomer(CustomerGateway repository) {
        return new UpdateCustomerImp(repository);
    }

    @Bean
    public DeleteCustomer deleteCustomer(CustomerGateway repository) {
        return new DeleteCustomerImp(repository);
    }

    @Bean
    public ListCustomers listCustomers(CustomerGateway repository) {
        return new ListCustomersImp(repository);
    }

    @Bean
    public FindCustomerById findCustomerById(CustomerGateway repository) {
        return new FindCustomerByIdImp(repository);
    }

    @Bean
    public FindCustomerByDocument findCustomerByDocument(CustomerGateway repository) {
        return new FindCustomerByDocumentImp(repository);
    }

    @Bean
    public VehicleGateway vehicleGateway(SpringVehicleRepository repository) {
        return new JpaVehicleGatewayAdapter(repository);
    }

    @Bean
    public AddVehicle addVehicle(VehicleGateway repository, CustomerGateway customerGateway) {
        return new AddVehicleImp(repository, customerGateway);
    }

    @Bean
    public UpdateVehicle updateVehicle(VehicleGateway repository) {
        return new UpdateVehicleImp(repository);
    }

    @Bean
    public DeleteVehicle deleteVehicle(VehicleGateway repository) {
        return new DeleteVehicleImp(repository);
    }

    @Bean
    public FindVehicleById findVehicleById(VehicleGateway repository) {
        return new FindVehicleByIdImp(repository);
    }

    @Bean
    public FindVehicleByPlate findVehicleByPlate(VehicleGateway repository) {
        return new FindVehicleByPlateImp(repository);
    }

    @Bean
    public FindVehiclesByCustomerId findVehiclesByCustomerId(VehicleGateway repository) {
        return new FindVehiclesByCustomerIdImp(repository);
    }

    @Bean
    public ListVehicles listVehicles(VehicleGateway repository) {
        return new ListVehiclesImp(repository);
    }

    @Bean
    public MechanicGateway mechanicGateway(SpringMechanicRepository springMechanicRepository) {
        return new JpaMechanicGatewayAdapter(springMechanicRepository);
    }

    @Bean
    public PasswordEncoderGateway passwordEncoderGateway(PasswordEncoder passwordEncoder) {
        return new BcryptPasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public CreateMechanic createMechanic(MechanicGateway mechanicGateway, PasswordEncoderGateway passwordEncoderGateway) {
        return new CreateMechanicImp(mechanicGateway, passwordEncoderGateway);
    }

    @Bean
    public PasswordVerifierGateway passwordVerifierGateway(PasswordEncoder passwordEncoder) {
        return new BcryptPasswordVerifierAdapter(passwordEncoder);
    }

    @Bean
    public CustomerController customerController(
            CreateCustomer createCustomer,
            UpdateCustomer updateCustomer,
            DeleteCustomer deleteCustomer,
            FindCustomerById findCustomerById,
            FindCustomerByDocument findCustomerByDocument,
            ListCustomers listCustomers) {
        return new CustomerController(
                createCustomer,
                updateCustomer,
                deleteCustomer,
                findCustomerById,
                findCustomerByDocument,
                listCustomers);
    }

    @Bean
    public AuthController authController(AuthenticateMechanic authenticateMechanic){
        return new AuthController(authenticateMechanic);
    }

    @Bean
    public AuthenticateMechanic authenticateMechanic(
            MechanicGateway mechanicGateway,
            PasswordVerifierGateway passwordVerifier,
            TokenGateway tokenGateway) {
        return new AuthenticateMechanicImp(mechanicGateway, passwordVerifier, tokenGateway);
    }

    @Bean
    public VehicleController vehicleController(
            AddVehicle addVehicle,
            UpdateVehicle updateVehicle,
            DeleteVehicle deleteVehicle,
            FindVehicleById findVehicleById,
            FindVehicleByPlate findVehicleByPlate,
            FindVehiclesByCustomerId findVehiclesByCustomerId,
            ListVehicles listVehicles
    ){
        return new VehicleController(
                addVehicle,
                updateVehicle,
                deleteVehicle,
                findVehicleById,
                findVehicleByPlate,
                findVehiclesByCustomerId,
                listVehicles
        );
    }

    @Bean
    public MechanicController mechanicController(
            CreateMechanic createMechanic) {
        return new MechanicController(createMechanic);
    }
}
