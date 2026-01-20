package br.com.pitflow.registry.infrastructure.configuration;

import br.com.pitflow.common.infrastructure.security.JwtService;
import br.com.pitflow.registry.application.AddVehicleImp;
import br.com.pitflow.registry.application.AuthenticateMechanicImp;
import br.com.pitflow.registry.application.CreateCustomerImp;
import br.com.pitflow.registry.application.CreateMechanicImp;
import br.com.pitflow.registry.application.DeleteCustomerImp;
import br.com.pitflow.registry.application.DeleteVehicleImp;
import br.com.pitflow.registry.application.FindCustomerByDocumentImp;
import br.com.pitflow.registry.application.FindCustomerByIdImp;
import br.com.pitflow.registry.application.FindVehicleByIdImp;
import br.com.pitflow.registry.application.FindVehicleByPlateImp;
import br.com.pitflow.registry.application.FindVehiclesByCustomerIdImp;
import br.com.pitflow.registry.application.ListCustomersImp;
import br.com.pitflow.registry.application.ListVehiclesImp;
import br.com.pitflow.registry.application.UpdateCustomerImp;
import br.com.pitflow.registry.application.UpdateVehicleImp;
import br.com.pitflow.registry.application.usecases.AddVehicle;
import br.com.pitflow.registry.application.usecases.AuthenticateMechanic;
import br.com.pitflow.registry.application.usecases.CreateCustomer;
import br.com.pitflow.registry.application.usecases.CreateMechanic;
import br.com.pitflow.registry.application.usecases.DeleteCustomer;
import br.com.pitflow.registry.application.usecases.DeleteVehicle;
import br.com.pitflow.registry.application.usecases.FindCustomerByDocument;
import br.com.pitflow.registry.application.usecases.FindCustomerById;
import br.com.pitflow.registry.application.usecases.FindVehicleById;
import br.com.pitflow.registry.application.usecases.FindVehicleByPlate;
import br.com.pitflow.registry.application.usecases.FindVehiclesByCustomerId;
import br.com.pitflow.registry.application.usecases.ListCustomers;
import br.com.pitflow.registry.application.usecases.ListVehicles;
import br.com.pitflow.registry.application.usecases.UpdateCustomer;
import br.com.pitflow.registry.application.usecases.UpdateVehicle;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaCustomerRepositoryAdapter;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaMechanicRepositoryAdapter;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaVehicleRepositoryAdapter;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringCustomerRepository;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringMechanicRepository;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringVehicleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanRegistryConfig {

    @Bean
    public CustomerRepository customerRepository(SpringCustomerRepository repository) {
        return new JpaCustomerRepositoryAdapter(repository);
    }

    @Bean
    public CreateCustomer createCustomer(CustomerRepository repository) {
        return new CreateCustomerImp(repository);
    }

    @Bean
    public UpdateCustomer updateCustomer(CustomerRepository repository) {
        return new UpdateCustomerImp(repository);
    }

    @Bean
    public DeleteCustomer deleteCustomer(CustomerRepository repository) {
        return new DeleteCustomerImp(repository);
    }

    @Bean
    public ListCustomers listCustomers(CustomerRepository repository) {
        return new ListCustomersImp(repository);
    }

    @Bean
    public FindCustomerById findCustomerById(CustomerRepository repository) {
        return new FindCustomerByIdImp(repository);
    }

    @Bean
    public FindCustomerByDocument findCustomerByDocument(CustomerRepository repository) {
        return new FindCustomerByDocumentImp(repository);
    }

    @Bean
    public VehicleRepository vehicleRepository(SpringVehicleRepository repository) {
        return new JpaVehicleRepositoryAdapter(repository);
    }

    @Bean
    public AddVehicle addVehicle(VehicleRepository repository, CustomerRepository customerRepository) {
        return new AddVehicleImp(repository, customerRepository);
    }

    @Bean
    public UpdateVehicle updateVehicle(VehicleRepository repository) {
        return new UpdateVehicleImp(repository);
    }

    @Bean
    public DeleteVehicle deleteVehicle(VehicleRepository repository) {
        return new DeleteVehicleImp(repository);
    }

    @Bean
    public FindVehicleById findVehicleById(VehicleRepository repository) {
        return new FindVehicleByIdImp(repository);
    }

    @Bean
    public FindVehicleByPlate findVehicleByPlate(VehicleRepository repository) {
        return new FindVehicleByPlateImp(repository);
    }

    @Bean
    public FindVehiclesByCustomerId findVehiclesByCustomerId(VehicleRepository repository) {
        return new FindVehiclesByCustomerIdImp(repository);
    }

    @Bean
    public ListVehicles listVehicles(VehicleRepository repository) {
        return new ListVehiclesImp(repository);
    }

    @Bean
    public MechanicRepository mechanicRepository(SpringMechanicRepository springMechanicRepository) {
        return new JpaMechanicRepositoryAdapter(springMechanicRepository);
    }

    @Bean
    public CreateMechanic createMechanic(MechanicRepository mechanicRepository, PasswordEncoder passwordEncoder) {
        return new CreateMechanicImp(mechanicRepository, passwordEncoder);
    }

    @Bean
    public AuthenticateMechanic authenticateMechanic(
            MechanicRepository mechanicRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        return new AuthenticateMechanicImp(mechanicRepository, passwordEncoder, jwtService);
    }
}
