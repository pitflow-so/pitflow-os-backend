package br.com.pitflow.inventory.infrastructure.config;

import br.com.pitflow.inventory.controller.PartController;
import br.com.pitflow.inventory.controller.ServiceController;
import br.com.pitflow.inventory.core.usecase.part.CreatePartImp;
import br.com.pitflow.inventory.core.usecase.service.CreateServiceImp;
import br.com.pitflow.inventory.core.usecase.part.DeletePartImp;
import br.com.pitflow.inventory.core.usecase.service.DeleteServiceImp;
import br.com.pitflow.inventory.core.usecase.part.FindPartByIdImp;
import br.com.pitflow.inventory.core.usecase.part.FindPartBySkuImp;
import br.com.pitflow.inventory.core.usecase.service.FindServiceByIdImp;
import br.com.pitflow.inventory.core.usecase.part.ListPartsImp;
import br.com.pitflow.inventory.core.usecase.service.ListServicesImp;
import br.com.pitflow.inventory.core.usecase.part.UpdatePartImp;
import br.com.pitflow.inventory.core.usecase.service.UpdateServiceImp;
import br.com.pitflow.inventory.core.usecase.part.inputPort.CreatePart;
import br.com.pitflow.inventory.core.usecase.service.inputPort.CreateService;
import br.com.pitflow.inventory.core.usecase.part.inputPort.DeletePart;
import br.com.pitflow.inventory.core.usecase.service.inputPort.DeleteService;
import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartById;
import br.com.pitflow.inventory.core.usecase.part.inputPort.FindPartBySku;
import br.com.pitflow.inventory.core.usecase.service.inputPort.FindServiceById;
import br.com.pitflow.inventory.core.usecase.part.inputPort.ListParts;
import br.com.pitflow.inventory.core.usecase.service.inputPort.ListServices;
import br.com.pitflow.inventory.core.usecase.part.inputPort.UpdatePart;
import br.com.pitflow.inventory.core.usecase.service.inputPort.UpdateService;
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
    public PartGateway partGateway(SpringPartRepository springPartRepository) {
        return new JpaPartGatewayAdapter(springPartRepository);
    }

    @Bean
    public ServiceGateway serviceGateway(SpringServiceRepository springServiceRepository) {
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

    @Bean
    public PartController partController(CreatePart createPart,
                                         FindPartById findPartById,
                                         FindPartBySku findPartBySku,
                                         ListParts listParts,
                                         UpdatePart updatePart,
                                         DeletePart deletePart)
    {
        return new  PartController(createPart, findPartById, findPartBySku, listParts, updatePart, deletePart);
    }

    @Bean
    public ServiceController serviceController(
            CreateService createService,
            FindServiceById findServiceById,
            ListServices listServices,
            UpdateService updateService,
            DeleteService deleteService
    ){
        return new ServiceController(createService,  findServiceById, listServices, updateService, deleteService);
    }
}
