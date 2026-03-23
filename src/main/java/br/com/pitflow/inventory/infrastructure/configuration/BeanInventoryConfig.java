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
import br.com.pitflow.inventory.core.gateway.PartGateway;
import br.com.pitflow.inventory.core.gateway.ServiceGateway;
import br.com.pitflow.inventory.infrastructure.persistence.adapter.JpaPartGatewayAdapter;
import br.com.pitflow.inventory.infrastructure.persistence.adapter.JpaServiceGatewayAdapter;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringPartRepository;
import br.com.pitflow.inventory.infrastructure.persistence.repository.SpringServiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanInventoryConfig {

    @Bean
    public PartGateway partRepository(SpringPartRepository springPartRepository) {
        return new JpaPartGatewayAdapter(springPartRepository);
    }

    @Bean
    public ServiceGateway serviceRepository(SpringServiceRepository springServiceRepository) {
        return new JpaServiceGatewayAdapter(springServiceRepository);
    }

    @Bean
    public CreatePart createPart(PartGateway partController) {
        return new CreatePartImp(partController);
    }

    @Bean
    public CreateService createService(ServiceGateway serviceGateway) {
        return new CreateServiceImp(serviceGateway);
    }

    @Bean
    public FindPartById findPartById(PartGateway partGateway) {
        return new FindPartByIdImp(partGateway);
    }

    @Bean
    public FindPartBySku findPartBySku(PartGateway partGateway) {
        return new FindPartBySkuImp(partGateway);
    }

    @Bean
    public ListParts listParts(PartGateway partGateway) {
        return new ListPartsImp(partGateway);
    }

    @Bean
    public UpdatePart updatePart(PartGateway partGateway) {
        return new UpdatePartImp(partGateway);
    }

    @Bean
    public DeletePart deletePart(PartGateway partGateway) {
        return new DeletePartImp(partGateway);
    }

    @Bean
    public FindServiceById findServiceById(ServiceGateway serviceGateway) {
        return new FindServiceByIdImp(serviceGateway);
    }

    @Bean
    public ListServices listServices(ServiceGateway serviceGateway) {
        return new ListServicesImp(serviceGateway);
    }

    @Bean
    public UpdateService updateService(ServiceGateway serviceGateway) {
        return new UpdateServiceImp(serviceGateway);
    }

    @Bean
    public DeleteService deleteService(ServiceGateway serviceGateway) {
        return new DeleteServiceImp(serviceGateway);
    }

}
