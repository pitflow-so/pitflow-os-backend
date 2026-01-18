package br.com.pitflow.registry.infrastructure.configuration;

import br.com.pitflow.registry.aplication.AddVehicleImp;
import br.com.pitflow.registry.aplication.CreateCustomerImp;
import br.com.pitflow.registry.aplication.DeleteCustomerImp;
import br.com.pitflow.registry.aplication.DeleteVehicleImp;
import br.com.pitflow.registry.aplication.FindCustomerByDocumentImp;
import br.com.pitflow.registry.aplication.FindCustomerByIdImp;
import br.com.pitflow.registry.aplication.FindVehicleByIdImp;
import br.com.pitflow.registry.aplication.FindVehicleByPlateImp;
import br.com.pitflow.registry.aplication.FindVehiclesByCustomerIdImp;
import br.com.pitflow.registry.aplication.ListCustomersImp;
import br.com.pitflow.registry.aplication.ListVehiclesImp;
import br.com.pitflow.registry.aplication.UpdateCustomerImp;
import br.com.pitflow.registry.aplication.UpdateVehicleImp;
import br.com.pitflow.registry.aplication.usecases.AddVehicle;
import br.com.pitflow.registry.aplication.usecases.CreateCustomer;
import br.com.pitflow.registry.aplication.usecases.DeleteCustomer;
import br.com.pitflow.registry.aplication.usecases.DeleteVehicle;
import br.com.pitflow.registry.aplication.usecases.FindCustomerByDocument;
import br.com.pitflow.registry.aplication.usecases.FindCustomerById;
import br.com.pitflow.registry.aplication.usecases.FindVehicleById;
import br.com.pitflow.registry.aplication.usecases.FindVehicleByPlate;
import br.com.pitflow.registry.aplication.usecases.FindVehiclesByCustomerId;
import br.com.pitflow.registry.aplication.usecases.ListCustomers;
import br.com.pitflow.registry.aplication.usecases.ListVehicles;
import br.com.pitflow.registry.aplication.usecases.UpdateCustomer;
import br.com.pitflow.registry.aplication.usecases.UpdateVehicle;
import br.com.pitflow.registry.domain.repository.CustomerRepository;
import br.com.pitflow.registry.domain.repository.VehicleRepository;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaCustomerRepositoryAdapter;
import br.com.pitflow.registry.infrastructure.persistence.adapter.JpaVehicleRepositoryAdapter;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringCustomerRepository;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringVehicleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
