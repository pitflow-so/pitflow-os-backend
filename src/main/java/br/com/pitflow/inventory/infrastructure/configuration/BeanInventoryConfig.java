package br.com.pitflow.inventory.infrastructure.configuration;

import br.com.pitflow.inventory.application.CreatePartImp;
import br.com.pitflow.inventory.application.CreateServiceImp;
import br.com.pitflow.inventory.application.DeletePartImp;
import br.com.pitflow.inventory.application.DeleteServiceImp;
import br.com.pitflow.inventory.application.FindPartByIdImp;
import br.com.pitflow.inventory.application.FindPartBySkuImp;
import br.com.pitflow.inventory.application.FindServiceByIdImp;
import br.com.pitflow.inventory.application.ListPartsImp;
import br.com.pitflow.inventory.application.ListServicesImp;
import br.com.pitflow.inventory.application.UpdatePartImp;
import br.com.pitflow.inventory.application.UpdateServiceImp;
import br.com.pitflow.inventory.application.usecase.CreatePart;
import br.com.pitflow.inventory.application.usecase.CreateService;
import br.com.pitflow.inventory.application.usecase.DeletePart;
import br.com.pitflow.inventory.application.usecase.DeleteService;
import br.com.pitflow.inventory.application.usecase.FindPartById;
import br.com.pitflow.inventory.application.usecase.FindPartBySku;
import br.com.pitflow.inventory.application.usecase.FindServiceById;
import br.com.pitflow.inventory.application.usecase.ListParts;
import br.com.pitflow.inventory.application.usecase.ListServices;
import br.com.pitflow.inventory.application.usecase.UpdatePart;
import br.com.pitflow.inventory.application.usecase.UpdateService;
import br.com.pitflow.inventory.domain.repository.PartRepository;
import br.com.pitflow.inventory.domain.repository.ServiceRepository;
import br.com.pitflow.inventory.infrastructure.persistence.adapter.JpaPartRepositoryAdapter;
import br.com.pitflow.inventory.infrastructure.persistence.adapter.JpaServiceRepositoryAdapter;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringPartRepository;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringServiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanInventoryConfig {

    @Bean
    public PartRepository partRepository(SpringPartRepository springPartRepository) {
        return new JpaPartRepositoryAdapter(springPartRepository);
    }

    @Bean
    public ServiceRepository serviceRepository(SpringServiceRepository springServiceRepository) {
        return new JpaServiceRepositoryAdapter(springServiceRepository);
    }

    @Bean
    public CreatePart createPart(PartRepository partController) {
        return new CreatePartImp(partController);
    }

    @Bean
    public CreateService createService(ServiceRepository serviceRepository) {
        return new CreateServiceImp(serviceRepository);
    }

    @Bean
    public FindPartById findPartById(PartRepository partRepository) {
        return new FindPartByIdImp(partRepository);
    }

    @Bean
    public FindPartBySku findPartBySku(PartRepository partRepository) {
        return new FindPartBySkuImp(partRepository);
    }

    @Bean
    public ListParts listParts(PartRepository partRepository) {
        return new ListPartsImp(partRepository);
    }

    @Bean
    public UpdatePart updatePart(PartRepository partRepository) {
        return new UpdatePartImp(partRepository);
    }

    @Bean
    public DeletePart deletePart(PartRepository partRepository) {
        return new DeletePartImp(partRepository);
    }

    @Bean
    public FindServiceById findServiceById(ServiceRepository serviceRepository) {
        return new FindServiceByIdImp(serviceRepository);
    }

    @Bean
    public ListServices listServices(ServiceRepository serviceRepository) {
        return new ListServicesImp(serviceRepository);
    }

    @Bean
    public UpdateService updateService(ServiceRepository serviceRepository) {
        return new UpdateServiceImp(serviceRepository);
    }

    @Bean
    public DeleteService deleteService(ServiceRepository serviceRepository) {
        return new DeleteServiceImp(serviceRepository);
    }

}
